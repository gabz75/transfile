package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;

/**
 * This class correspond to a complete datagram of the ID request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqId extends Requests
{

    public RqId()
    {
        super(RequestsEnum.ID);
    }

    @Override
    public byte[] buildDatas()
    {
        return super.buildDatas();
    }

    /**
     * Decodes a RqId from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static RqId decode(ByteBuffer bbr)
    {
        return new RqId();
    }

}
