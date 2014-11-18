package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class correspond to a complete datagram of the Get request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqGet extends Requests
{
    /**
     * The fileName asked for the get command
     */
    private final String fileName;

    public RqGet(String fileName)
    {
        super(RequestsEnum.GET);
        this.fileName = fileName;
    }

    /**
     * Return the fileName asked in the get
     * 
     * @return the fileName
     * 
     */
    public String getFileName()
    {
        return fileName;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        byte[] name = fileName.getBytes(Settings.ENCODING);
        byte[] delimiter = { 0 };

        ByteBuffer buffer = ByteBuffer.allocate(parent.length + name.length
                + delimiter.length);
        buffer.put(parent);
        buffer.put(name);
        buffer.put(delimiter);

        return buffer.array();
    }

    /**
     * Decodes a RqGet from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        String name = new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new RqGet(name);
    }

    @Override
    public String toString()
    {
        return "Get `" + fileName + "`";
    }

}
