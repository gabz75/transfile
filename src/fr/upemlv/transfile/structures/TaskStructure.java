package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * 
 * Represents a Task/Download
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class TaskStructure implements TransfilePackets
{

    /**
     * The file id
     */
    private final int id;
    
    
    /**
     * The file name 
     */
    private final String name;
    
    
    /**
     * The total length in bytes of a TaskStructure  
     */
    private int length;

    /**
     * Constructor
     * @param id the fileId
     * @param name the fileName
     */
    public TaskStructure(int id, String name)
    {
        this.id = id;
        this.name = name;
        length = Integer.SIZE / 8 + name.getBytes(Settings.ENCODING).length
                + Byte.SIZE / 8;
    }

    /**
     * gets the file id
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the file name
     * @return name
     */
    public String getName()
    {
        return name;
    }

    @Override
    public byte[] buildDatas()
    {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.putInt(id);
        buffer.put(name.getBytes(Settings.ENCODING));
        buffer.put((byte) 0);
        return buffer.array();
    }

    /**
     * Decodes a TaskStructure from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new Instance of a TaskStructure
     * @throws UncompletedPackageException
     */
    public static TaskStructure decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int id = bbr.getInt();
        String name = new String(Utils.decodeString(bbr), Settings.ENCODING);
        return new TaskStructure(id, name);
    }

    @Override
    public String toString()
    {
        return "ID = " + id + "\t Name : " + name;
    }

    /**
     * Gets the total length
     * @return length
     */
    public int getLength()
    {
        return length;
    }

}
