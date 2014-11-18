package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;

/**
 * This class correspond to a complete datagram of the Kill request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqKill extends Requests
{
    /**
     * The ID of the download to kill
     */
    private final int fileID;

    public RqKill(int id)
    {
        super(RequestsEnum.KILL);
        fileID = id;
    }

    /**
     * Return the id of the download to kill
     * 
     * @return the id of the download to kill
     */
    public int getFileId()
    {
        return fileID;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();

        ByteBuffer bb = ByteBuffer.allocate(parent.length + Integer.SIZE / 8);

        bb.put(parent);
        bb.putInt(fileID);

        return bb.array();
    }

    /**
     * Decodes a RqKill from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static RqKill decode(ByteBuffer bbr)
    {
        return new RqKill(bbr.getInt());
    }

    @Override
    public String toString()
    {
        return "Kill fileID : " + fileID;
    }

}
