package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class correspond to a complete datagram of the CD request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqCd extends Requests
{
    private final String fileName;

    public RqCd(String fName)
    {
        super(RequestsEnum.CD);
        fileName = fName;
    }

    /**
     * Return the fileName asked for the request command
     * 
     * @return the fileName argument of the RqCd
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

        return Utils.mergeByteArrays(parent, name, delimiter);
    }

    /**
     * Decodes a RqCd from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static RqCd decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        byte[] stringDecoded = Utils.decodeString(bbr);

        return new RqCd(new String(stringDecoded, Settings.ENCODING));
    }

    @Override
    public String toString()
    {
        return "Cd `" + getFileName() + "`";
    }
}
