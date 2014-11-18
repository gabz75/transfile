package fr.upemlv.transfile.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import fr.upemlv.transfile.enums.OpCodesEnum;
import fr.upemlv.transfile.enums.TransfileEnums;
import fr.upemlv.transfile.enums.TransfileEnumsInstanciable;
import fr.upemlv.transfile.exceptions.IllegalRequestException;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.utils.Logger;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class is design to analyze the data stored in a reader ByteBuffer, It
 * will try to parse the content of the byteBuffer.
 * 
 * If the content can't be decoded, then an exception will be thrown by the
 * fr.upemlv.transfile.packets.* classes. And the decoder will save the data,
 * and return null. The next time it will try to decode the news data red
 * combined with the data stored in the backup Buffer.
 * 
 * This class contains a DebugMode, useful to detect any problem, this DebugMode
 * must be precise in the constructor. Warning, writing in the file log is
 * extremely expensive in term of treatment.
 * 
 * IT MUST NOT BE USED IN A PRODUCTION ENVIRONNMENT ! Otherwise it will degrade
 * the performance of the downloads
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class TransfileDecoder
{
    /**
     * The current ByteBuffer
     */
    private ByteBuffer bbr;

    /**
     * The debug Mode
     */
    private final boolean debug;

    /**
     * The backup ByteBuffer
     */
    private ByteBuffer backupDatas = null;

    /**
     * The logger
     */
    private Logger logger;

    public TransfileDecoder(boolean debug, String log)
    {
        this.debug = debug;
        if (this.debug) {
            logger = new Logger("logs" + log);
        }
    }

    /**
     * Return if there is any other datas in the backup ByteBuffer
     * 
     * @return true if data remains in the backup ByteBuffer, otherwise false
     */
    public boolean hasRemaining()
    {
        if (backupDatas == null) {
            return false;
        }

        return backupDatas.hasRemaining();
    }

    /**
     * Try to decode a TransfilePackets on the given ByteBuffer
     * 
     * @param bb
     *            the ByteBuffe filled by the red datas
     * @return a TransfilePacket if the decode success, otherwise it will return
     *         null and backup the datas for the next try.
     * @throws IllegalRequestException
     */
    public TransfilePackets decode(ByteBuffer bb)
            throws IllegalRequestException
    {
        if (bb == null && hasRemaining()) {
            bb = backupDatas;
            backupDatas = null;
        }

        if (debug) {
            logger.print("Backup : ");
            if (backupDatas != null) {
                logger.print(Arrays.toString(backupDatas.array()));
            }
            logger.print("Buffer de lecture : ");
            if (bb != null) {
                logger.print(Arrays.toString(bb.array()));
            }
        }

        TransfilePackets packet = null;
        if (backupDatas == null) {
            bbr = bb;
            backupDatas = ByteBuffer.allocate(bb.capacity());
        } else {
            bb.flip();
            backupDatas.flip();

            int length = bb.limit() + backupDatas.limit();
            bbr = ByteBuffer.allocate(length);
            bbr.put(backupDatas);
            bbr.put(bb);
            backupDatas = ByteBuffer.allocate(length);
        }

        if (debug) {
            logger.print("Fusion du backup + lecture : ");
            logger.print(Arrays.toString(bbr.array()));
        }

        bbr.flip();

        try {
            /**
             * We get the operand code to know which type of command it is. (RRQ
             * | INF | ERR |DATA)
             */
            OpCodesEnum opCode = decodeOpCode();

            /**
             * Then we get the type of the command
             */
            TransfileEnumsInstanciable format = decodeFormat(opCode
                    .getEnumOfCode());

            /**
             * We return the Object corresponding to the command filled with the
             * data in the buffers
             */
            packet = format.getInstance(bbr);

            /**
             * If no exception has occurred, the packet is correctly decoded and
             * we erase the data red and set the position to the next data to
             * read
             */
            compactDatas();
        } catch (BufferUnderflowException e) {
            /**
             * If we enter in that case it means that we have not enough data to
             * decode the packet, so we clear the limit and go back to the old
             * position, to complete the data with the read
             */
            if (debug)
                e.printStackTrace(logger.getWriter());
            backupDatas();
        } catch (NegativeArraySizeException e) {
            if (debug)
                e.printStackTrace(logger.getWriter());
            backupDatas();
        } catch (UncompletedPackageException e) {
            if (debug)
                e.printStackTrace(logger.getWriter());
            backupDatas();
        } catch (IllegalRequestException e) {
            /**
             * If we found a bad request, we set the position to the limit (or
             * the old position before flip), we reset the buffer limit to the
             * capacity, and then compact data to erase all the data of the bad
             * request
             */
            if (debug)
                e.printStackTrace(logger.getWriter());
            compactDatas();
            throw new IllegalRequestException("Bad request");
        }

        if (packet != null && debug) {
            logger.print("return packet : " + packet.toString());
            if (backupDatas != null) {
                logger.print("Info BackupBuffer : " + backupDatas.position()
                        + " / " + backupDatas.limit() + " ["
                        + backupDatas.capacity() + "]");
            }
        }

        return packet;
    }

    /**
     * Save the data in the current ByteBuffer, and save them to tye backup
     * ByteBuffer
     */
    private void backupDatas()
    {
        bbr.rewind();
        backupDatas.put(bbr);
        bbr.clear();
    }

    /**
     * Compact the data in case of a successful decoding.
     */
    private void compactDatas()
    {
        if (bbr.hasRemaining()) {
            backupDatas.put(bbr);
        } else {
            backupDatas = null;
        }
        bbr.clear();
    }

    /**
     * Decode the Operand Code of the header, and return it.
     * 
     * If the operand code red isn't recognize it will throw an exception.
     * 
     * @return the operand code
     * @throws IllegalRequestException
     */
    private OpCodesEnum decodeOpCode() throws IllegalRequestException
    {
        OpCodesEnum oC = (OpCodesEnum) Utils.fetch(OpCodesEnum.values(),
                bbr.get());

        if (oC == null) {
            throw new IllegalRequestException("Operand Code not recognize");
        }

        return oC;
    }

    /**
     * Decode the second byte of the header representing the Format of the
     * packet (RRQ | INF | ERR | DATAS) and return the Enums associated.
     * 
     * It will be based on the previously operand code decoded and its enums
     * associated.
     * 
     * If the format doesn't exist il will throw an exception
     * 
     * @param eCode
     * @return the format of the packet
     * @throws IllegalRequestException
     */
    private TransfileEnumsInstanciable decodeFormat(
            Class<? extends TransfileEnums> eCode)
            throws IllegalRequestException
    {
        TransfileEnums format = Utils.fetch(eCode, bbr.get());

        if (!(format instanceof TransfileEnumsInstanciable)) {
            throw new IllegalRequestException("Format Code not recognize");
        }

        return (TransfileEnumsInstanciable) format;
    }
}
