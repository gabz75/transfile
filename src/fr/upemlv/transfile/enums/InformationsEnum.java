package fr.upemlv.transfile.enums;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.informations.InfoCd;
import fr.upemlv.transfile.packets.informations.InfoGet;
import fr.upemlv.transfile.packets.informations.InfoId;
import fr.upemlv.transfile.packets.informations.InfoKill;
import fr.upemlv.transfile.packets.informations.InfoLs;
import fr.upemlv.transfile.packets.informations.InfoTcpRequired;
import fr.upemlv.transfile.packets.informations.InfoUdpRequired;

/**
 * Represents the informations
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public enum InformationsEnum implements TransfileEnums,
        TransfileEnumsInstanciable
{
    CD((byte) 0) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoCd.decode(bbr);
        }
    },

    LS((byte) 1) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoLs.decode(bbr);
        }
    },

    GETCONFIRMED((byte) 2) {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoGet.decode(bbr);
        }
    },

    UDPREQUIRED((byte) 3) {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoUdpRequired.decode(bbr);
        }
    },

    TCPREQUIRED((byte) 4) {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoTcpRequired.decode(bbr);
        }
    },

    KILL((byte) 5) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoKill.decode(bbr);
        }
    },

    ID((byte) 6) {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return InfoId.decode(bbr);
        }
    };

    /**
     * Constructor
     * @param iC the information code
     */
    private InformationsEnum(byte iC)
    {
        informationCode = iC;
    }

    /**
     * the information code
     */
    private final byte informationCode;

    @Override
    public int getLength()
    {
        return Byte.SIZE / 8;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[] { informationCode };
    }

    @Override
    abstract public TransfilePackets getInstance(ByteBuffer bbr)
            throws UncompletedPackageException;
}