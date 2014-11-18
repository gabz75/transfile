package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.packets.Informations;

/**
 * Represents an Information ID
 * It contains the current user ID, it is used
 * to log the user.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoId extends Informations
{
    /**
     * user id
     */
    private final int id;

    /**
     * Constructor
     * @param id user id
     */
    public InfoId(int id)
    {
        super(InformationsEnum.ID);
        this.id = id;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] header = super.buildDatas();

        ByteBuffer bb = ByteBuffer.allocate(header.length + Integer.SIZE / 8);
        bb.put(header);
        bb.putInt(id);

        return bb.array();
    }

    /**
     * Gets the user id
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Decodes an InfoId from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new Instance of InfoId
     */
    public static InfoId decode(ByteBuffer bbr)
    {
        int id = bbr.getInt();

        return new InfoId(id);
    }
}
