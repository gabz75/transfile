package fr.upemlv.transfile.enums;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;

/**
 * Represents an Instanciable Enum
 * 
 * This interface define that the enums who implements this interface will be
 * instantiable via the getInstance method
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 * 
 */
public interface TransfileEnumsInstanciable
{
    /**
     * Gets an Instance of the correct packet corresponding to the ByteBuffer
     * given in parameter.
     * 
     * @param bbr
     *            the ByteBuffer
     * @return an instance of the correct packet
     * @throws UncompletedPackageException
     */
    abstract public TransfilePackets getInstance(ByteBuffer bbr)
            throws UncompletedPackageException;
}
