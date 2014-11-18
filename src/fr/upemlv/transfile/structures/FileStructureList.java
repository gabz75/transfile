package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;

/**
 * 
 * Represents a List of FileStructures
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class FileStructureList implements TransfilePackets
{
    /**
     * The list of FileStructures
     */
    private final List<FileStructure> fileList;
    
    /**
     * The length of the longer FileStructure's file name
     */
    private int maxNameLength = 0;
    
    /**
     * The total length in bytes of the FileStructureList 
     */
    private int length;

    /**
     * Constructor
     */
    public FileStructureList()
    {
        fileList = new ArrayList<FileStructure>();
        length = Integer.SIZE / 8;
    }

    /**
     * Adds the FileStructure given in parameter to the list.
     * Updates the maxNameLength for any FileStructure in the list
     * @param f the FileStructure
     */
    public void addFile(FileStructure f)
    {
        fileList.add(f);
        length += f.getLength();
        if (f.getFileName().length() >= maxNameLength)
            maxNameLength = f.getFileName().length();
        for (FileStructure fileStructures : fileList) {
            fileStructures.setSpacer(maxNameLength
                    - fileStructures.getFileName().length());
        }
    }

    /**
     * Gets the total length
     * @return the length
     */
    public int getLength()
    {
        return length;
    }

    @Override
    public byte[] buildDatas()
    {
        ByteBuffer bb = ByteBuffer.allocate(length);

        bb.putInt(fileList.size());
        for (FileStructure fileStructures : fileList) {
            bb.put(fileStructures.buildDatas());

        }

        return bb.array();
    }

    /**
     * Decodes a FileStructureList from the given ByteBuffer in parameter
     * @param bbr the ByteBuffer
     * @return a new Instance of a FileStructureList
     * @throws UncompletedPackageException
     */
    public static FileStructureList decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        FileStructureList list = new FileStructureList();
        int nbFile = bbr.getInt();
        for (int i = 0; i < nbFile; i++) {
            list.addFile(FileStructure.decode(bbr));
        }

        return list;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (FileStructure f : fileList) {
            sb.append(f.toString() + "\n");
        }

        return sb.toString();
    }
}
