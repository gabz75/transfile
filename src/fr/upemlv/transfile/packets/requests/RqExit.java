package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.packets.Requests;

/**
 * This class represent an exit request to kill the command SocketChannel,
 * including all the download channels.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 * 
 */
public class RqExit extends Requests
{

    public RqExit()
    {
        super(RequestsEnum.EXIT);
    }

    @Override
    public byte[] buildDatas()
    {
        return super.buildDatas();
    }

    /**
     * Decode an exit request and return a the object corresponding
     * @param bbr
     * @return the RqExit
     */
    public static RqExit decode(ByteBuffer bbr)
    {
        return new RqExit();
    }
}
