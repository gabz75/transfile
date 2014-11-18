package fr.upemlv.transfile.enums;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.data.DataTCP;
import fr.upemlv.transfile.packets.data.DataUDP;

/**
 * Represents the datas
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public enum DatasEnum implements TransfileEnums, TransfileEnumsInstanciable
{
    TCP((byte) 1) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return DataTCP.decode(bbr);
        }
    },

    UDP((byte) 2) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return DataUDP.decode(bbr);
        }
    };

    /**
     * The value of the protocol
     */
    private final byte protCode;

    
    /**
     * Constructor
     * @param b a byte corresponding to the protocol
     */
    DatasEnum(byte b)
    {
        this.protCode = b;
    }

    
    /**
     * Gets the protocol code
     * @return the protocol code
     */
    public byte getProtCode()
    {
        return protCode;
    }

    @Override
    public int getLength()
    {
        return Byte.SIZE / 8;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[] { protCode };
    }

    @Override
    abstract public TransfilePackets getInstance(ByteBuffer bbr)
            throws UncompletedPackageException;
}
