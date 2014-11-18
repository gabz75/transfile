package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * Represents an Information Get. 
 * It is sent by the server in response of a Request Get.
 * 
 * It contains all the information required upon the file desired.
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoGet extends Informations
{

    /**
     * The file id
     */
    private final int id;
    
    /**
     * The file Name
     */
    private final String fileName;
    
    
    /**
     * the total number of fragments required
     */
    private final int totalFragment;

    /**
     * Constructor
     * @param id file id
     * @param fileName file name
     * @param total total number of fragments
     */
    public InfoGet(int id, String fileName, int total)
    {
        super(InformationsEnum.GETCONFIRMED);
        this.id = id;
        this.fileName = fileName;
        this.totalFragment = total;
    }

    /**
     * Gets file id
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets File Name
     * @return fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Gets total number of fragments
     * @return totalFragment
     */
    public int getTotalFragment()
    {
        return totalFragment;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        byte[] name = fileName.getBytes(Settings.ENCODING);
        byte[] delimiter = { 0 };

        ByteBuffer buffer = ByteBuffer.allocate(parent.length + name.length
                + delimiter.length + 2 * (Integer.SIZE / 8));
        buffer.put(parent);
        buffer.putInt(id);
        buffer.putInt(totalFragment);
        buffer.put(name);
        buffer.put(delimiter);

        return buffer.array();
    }

    /**
     * Decodes an InfoGet from the given ByteBuffer given in parameter
     * @param bbr the ByteBuffer
     * @return a new Instance of InfoGet
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int id = bbr.getInt();
        int totalFragment = bbr.getInt();
        String name = new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new InfoGet(id, name, totalFragment);
    }

    @Override
    public String toString()
    {
        return "Get id : " + id + ", fileName : " + fileName
                + " totalFragment : " + totalFragment;
    }

}
