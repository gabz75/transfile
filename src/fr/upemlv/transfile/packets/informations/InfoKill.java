package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

/**
 * 
 * Represents an Information Kill.
 * It is sent by the server after a Request Kill.
 * it contains a message to print on screen and the file id concerned.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoKill extends Informations
{
    /**
     * the message
     */
    private final String message;
    
    
    /**
     * The file id 
     */
    private final int id;

    /**
     * Constructor
     * @param id file id
     */
    public InfoKill(int id)
    {
        super(InformationsEnum.KILL);
        this.id = id;
        message = "Task " + id + " killed successfully.";
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        byte[] name = message.getBytes(Settings.ENCODING);
        byte[] delimiter = { 0 };

        ByteBuffer buffer = ByteBuffer.allocate(parent.length + name.length
                + delimiter.length + Integer.SIZE / 8);
        buffer.put(parent);
        buffer.putInt(id);
        buffer.put(name);
        buffer.put(delimiter);

        return buffer.array();
    }

    /**
     * Decodes an InfoKill from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new instance of InfoKill
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int id = bbr.getInt();
        new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new InfoKill(id);
    }

    @Override
    public String toString()
    {
        return message;
    }

    /**
     * Gets the file id
     * @return id
     */
    public int getId()
    {
        return id;
    }
}
