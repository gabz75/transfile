package fr.upemlv.transfile.packets.requests;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.settings.Settings;

/**
 * This class correspond to a complete datagram of the Download request command
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class RqDownload extends Requests
{

    /**
     * The ID of the download
     */
    private final int id;

    /**
     * The number of the starting fragment asked
     */
    private final int startFragment;

    /**
     * The number of the last fragment, if the value is 0, it means that end
     * last fragment will be the last fragment of the file based on the length
     * measured in byte
     */
    private final int endFragment;

    /**
     * Define if it is the last download request, used to shutdown the reading
     * SocketChannel of a Data transfer SocketChannel
     */
    private final int isLast;

    public RqDownload(int id, int start, int end, int isLast)
    {
        super(RequestsEnum.DOWNLOAD);
        this.id = id;
        this.startFragment = start;
        this.endFragment = end;
        this.isLast = isLast;
    }

    /**
     * @return true if this is the last request download
     */
    public boolean isLast()
    {
        return (isLast == Settings.LAST_FRAGMENT) ? true : false;
    }

    /**
     * @return the ID of the download
     */
    public int getId()
    {
        return id;
    }

    /**
     * Return the start number fragment
     * 
     * @return the start fragment
     */
    public int getStartFragment()
    {
        return startFragment;
    }

    /**
     * Return the end number fragment
     * 
     * @return the end fragment
     */
    public int getEndFragment()
    {
        return endFragment;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] header = super.buildDatas();
        ByteBuffer bb = ByteBuffer.allocate(header.length
                + (4 * (Integer.SIZE / 8)));

        bb.put(header);
        bb.putInt(id);
        bb.putInt(startFragment);
        bb.putInt(endFragment);
        bb.putInt(isLast);

        return bb.array();
    }

    /**
     * Decodes a RqDownload from the given ByteBuffer given in parameter
     * 
     * @param bbr
     *            the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static RqDownload decode(ByteBuffer bbr)
    {
        int id = bbr.getInt();
        int start = bbr.getInt();
        int end = bbr.getInt();
        int isLast = bbr.getInt();
        return new RqDownload(id, start, end, isLast);
    }

    @Override
    public String toString()
    {
        return "Download id : " + id + ", startFragment : " + startFragment
                + ", endFragment : " + endFragment + " isLast : " + isLast();
    }
}
