package fr.upemlv.transfile.enums;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.errors.AccessDenied;
import fr.upemlv.transfile.packets.errors.BadRequest;
import fr.upemlv.transfile.packets.errors.NotExistantFileOrDirectory;
import fr.upemlv.transfile.packets.errors.NotExistantTask;

/**
 * Represents the Errors
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public enum ErrorsEnum implements TransfileEnums, TransfileEnumsInstanciable
{
    NOT_EXITANT_FILE_DIRECTORY((byte) 0) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return NotExistantFileOrDirectory.decode(bbr);
        }
    },

    NOT_EXISTANT_TASK((byte) 1) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return NotExistantTask.decode(bbr);
        }
    },

    ACCESS_DENIED((byte) 2) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return AccessDenied.decode(bbr);
        }
    },

    UDP_DENIED((byte) 3) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            // @todo
            return null;
        }
    },

    BAD_REQUEST((byte) 5) {
        @Override
        public TransfilePackets getInstance(ByteBuffer bbr)
                throws UncompletedPackageException
        {
            return BadRequest.decode(bbr);
        }
    };

    /**
     * Constructor
     * @param iC the error code 
     */
    private ErrorsEnum(byte iC)
    {
        errorCode = iC;
    }

    /**
     * the error code
     */
    private final byte errorCode;

    @Override
    public int getLength()
    {
        return Byte.SIZE / 8;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[] { errorCode };
    }

    @Override
    abstract public TransfilePackets getInstance(ByteBuffer bbr)
            throws UncompletedPackageException;
}