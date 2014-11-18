package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * 
 * Represents the structure of a File
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class FileStructure implements TransfilePackets
{
    /**
     * The size in bits
     */
    private final int fileSize;

    /**
     * the FileDate
     */
    private final FileDate fileDate;

    /**
     * the file name
     */
    private final String fileName;

    /**
     * the total length in bytes of a FileStructure
     */
    private int length;

    /**
     * The necessary number of " " 
     */
    private int spacer;

    /**
     * The type of the File.
     * 
     * D : Directory
     * F : File
     * 
     * 
     */
    private final String type;

    /**
     * Constructor 
     * @param size the size of the file
     * @param date the FileDate
     * @param name the file name
     * @param type the type
     */
    public FileStructure(int size, FileDate date, String name, String type)
    {
        fileSize = size;
        fileDate = date;
        fileName = name;
        this.type = type;
        length = 4 + fileDate.getLength()
                + fileName.getBytes(Settings.ENCODING).length + 1
                + type.getBytes(Settings.ENCODING).length + 1;
    }

    /**
     * Gets the type
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets the file name
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Gets the length of a FileStructure
     * @return the length
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Sets the correct number for the spacer
     * @param spacer the value
     */
    public void setSpacer(int spacer)
    {
        this.spacer = spacer;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] nameByte = fileName.getBytes(Settings.ENCODING);
        byte[] dateByte = fileDate.buildDatas();
        byte[] typeByte = type.getBytes(Settings.ENCODING);
        ByteBuffer bb = ByteBuffer.allocate(length);

        bb.putInt(fileSize);
        bb.put(dateByte);
        bb.put(nameByte);
        bb.put((byte) 0);
        bb.put(typeByte);
        bb.put((byte) 0);

        return bb.array();
    }

    /**
     * Creates a String composed with the correct number of " " to 
     * see a clean delimitation between all the pieces of information.
     * @return the delimiter
     */
    public String getDelimiter()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spacer; i++) {
            sb.append(" ");
        }
        sb.append("   ");
        return sb.toString();
    }

    /**
     * Decodes a FileStructure from the given ByteBuffer in parameter
     * @param bbr the ByteBuffer
     * @return a new Instance of FileStructure
     * @throws UncompletedPackageException
     */
    public static FileStructure decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int size = bbr.getInt();
        FileDate date = FileDate.decode(bbr);
        String name = new String(Utils.decodeString(bbr), Settings.ENCODING);
        String type = new String(Utils.decodeString(bbr), Settings.ENCODING);
        return new FileStructure(size, date, name, type);
    }

    @Override
    public String toString()
    {
        return type + "\t" + fileName + getDelimiter() + fileDate.toString()
                + "\t\t" + fileSize / 1024 + " Ko";
    }
}
