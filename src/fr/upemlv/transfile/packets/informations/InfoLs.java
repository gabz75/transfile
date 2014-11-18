package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.structures.FileStructureList;

/**
 * Represents an Information LS.
 * it is sent by the server after a Request LS.
 * It contains a FileStructureList.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoLs extends Informations
{

    /**
     * The FileStructureList
     */
    private final FileStructureList fileList;

    /**
     * Constructor
     * @param fList the list
     */
    public InfoLs(FileStructureList fList)
    {
        super(InformationsEnum.LS);
        fileList = fList;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] header = super.buildDatas();

        ByteBuffer bb = ByteBuffer.allocate(header.length
                + fileList.getLength());

        bb.put(header);
        bb.put(fileList.buildDatas());

        return bb.array();
    }

    /**
     * Decodes an InfoLs from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new instance of InfoLs
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        FileStructureList list = FileStructureList.decode(bbr);

        return new InfoLs(list);
    }

    @Override
    public String toString()
    {
        return fileList.toString();
    }

}
