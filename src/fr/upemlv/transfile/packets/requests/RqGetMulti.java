package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.packets.TransfilePackets;

/**
 * This class correspond to a complete datagram of the get-multi request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqGetMulti extends Requests
{
    /**
     * The id of the file to download in multicast
     */
    private final int id;

    public RqGetMulti(int id)
    {
        super(RequestsEnum.GETMULTI);
        this.id = id;
    }

    /**
     * Return the id of the file to download
     * @return the id of the file
     */
    public int getId()
    {
        return id;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        ByteBuffer buffer = ByteBuffer.allocate(parent.length + Integer.SIZE
                / 8);
        buffer.put(parent);
        buffer.putInt(id);

        return buffer.array();
    }

    /**
     * Decodes a RqGetMulti from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
    {
        int id = bbr.getInt();

        return new RqGetMulti(id);
    }

    @Override
    public String toString()
    {
        return "GetMulti id : " + id;
    }
}
