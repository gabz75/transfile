package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * 
 * Represents an Information CD.
 * It is sent by the server after a Request CD.
 * It contains the new current location of the server.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoCd extends Informations
{
    /**
     * The new Server's Path
     */
    private String message;

    /**
     * Constructor
     * @param message the message
     */
    public InfoCd(String message)
    {
        super(InformationsEnum.CD);
        this.message = message;
    }

    /**
     * Gets the message
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        byte[] name = message.getBytes(Settings.ENCODING);
        byte[] delimiter = { 0 };

        return Utils.mergeByteArrays(parent, name, delimiter);
    }

    /**
     * Decodes an InfoCd from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new Instance of InfoCd
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        String msg = new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new InfoCd(msg);
    }

    @Override
    public String toString()
    {
        return message;
    }

}
