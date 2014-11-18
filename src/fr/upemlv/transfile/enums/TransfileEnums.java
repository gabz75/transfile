package fr.upemlv.transfile.enums;

/**
 * Represents a Transfile Enum
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public interface TransfileEnums
{
    /**
     * Gets the number of bits used to represent this enum.
     * @return the length of the enum in bits
     */
    public int getLength();

    /**
     * Gets a bytes' array corresponding to the enum
     * @return the datas
     */
    public byte[] getBytes();
}
