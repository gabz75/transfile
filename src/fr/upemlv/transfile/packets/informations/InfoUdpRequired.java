package fr.upemlv.transfile.packets.informations;

import java.nio.ByteBuffer;


import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.structures.Address;
import fr.upemlv.transfile.utils.Utils;


/**
 * Represents an InformationUdpRequired.
 * It is sent by the server to make the client
 * switch into the UDP transfer mod.
 * It contains the server address, a message and the file id.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class InfoUdpRequired extends Informations
{

    /**
     * The server address
     */
    private final Address address;
    
    /**
     * The message 
     */
    private final String message;
    
    
    /**
     *  The file id
     */
    private final int id;

    /**
     * Constructor
     * @param id the file id
     * @param address the server address
     * @param msg the message
     */
    public InfoUdpRequired(int id, Address address, String msg)
    {
        super(InformationsEnum.UDPREQUIRED);
        this.address = address;
        this.message = msg;
        this.id = id;
    }

    /**
     * Gets the file id
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the server Address
     * @return address
     */
    public Address getAddress()
    {
        return address;
    }

    /**
     * Gets the message
     * @return message
     */
    public String getMessage()
    {
        return message;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] parent = super.buildDatas();
        byte[] address = this.address.buildDatas();
        byte[] msg = message.getBytes(Settings.ENCODING);
        byte[] delimiter = new byte[] { 0 };

        ByteBuffer buffer = ByteBuffer.allocate(parent.length + address.length
                + msg.length + delimiter.length + Integer.SIZE / 8);
        buffer.put(parent);
        buffer.putInt(id);
        buffer.put(address);
        buffer.put(msg);
        buffer.put(delimiter);
        return buffer.array();
    }

    
    /**
     * Decodes an InfoUdpRequired from the given ByteBuffer
     * @param bbr the ByteBuffer
     * @return a new instance of InfoUdpRequired
     * @throws UncompletedPackageException
     */
    public static TransfilePackets decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int id = bbr.getInt();
        Address address = Address.decode(bbr);
        String msg = new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new InfoUdpRequired(id, address, msg);
    }

    @Override
    public String toString()
    {
        return message + " " + address + " " + id;
    }
}
