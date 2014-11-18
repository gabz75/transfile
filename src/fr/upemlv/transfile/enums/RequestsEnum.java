package fr.upemlv.transfile.enums;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.requests.RqCd;
import fr.upemlv.transfile.packets.requests.RqDownload;
import fr.upemlv.transfile.packets.requests.RqExit;
import fr.upemlv.transfile.packets.requests.RqGet;
import fr.upemlv.transfile.packets.requests.RqGetMulti;
import fr.upemlv.transfile.packets.requests.RqId;
import fr.upemlv.transfile.packets.requests.RqKill;
import fr.upemlv.transfile.packets.requests.RqLs;

/**
 * Reprensents the requests
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 * 
 */
public enum RequestsEnum implements TransfileEnums, TransfileEnumsInstanciable
{
    CD((byte) 0, "cd") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqCd.decode(bbr);
        }
    },

    LS((byte) 1, "ls") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqLs.decode(bbr);
        }
    },

    GET((byte) 2, "get") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqGet.decode(bbr);
        }
    },

    GETMULTI((byte) 3, "get-multicast") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqGetMulti.decode(bbr);
        }
    },

    KILL((byte) 4, "kill") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqKill.decode(bbr);
        }
    },

    DOWNLOAD((byte) 5, "") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqDownload.decode(bbr);
        }
    },

    ID((byte) 6, "") {

        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqId.decode(bbr);
        }

    },

    EXIT((byte) 7, "exit") {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return RqExit.decode(bbr);
        }
    };

    /**
     * Constructor
     * 
     * @param b
     *            the request code
     * @param name
     *            the name of the request
     */
    private RequestsEnum(byte b, String name)
    {
        requestCode = b;
        this.name = name;
    }

    /**
     * the request code
     */
    private final byte requestCode;

    /**
     * the request name
     */
    private final String name;

    @Override
    public int getLength()
    {
        return Byte.SIZE / 8;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[] { requestCode };
    }

    public String getName()
    {
        return name;
    }

    @Override
    abstract public TransfilePackets getInstance(ByteBuffer bbr)
            throws UncompletedPackageException;
}