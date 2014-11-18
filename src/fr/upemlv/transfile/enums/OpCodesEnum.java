package fr.upemlv.transfile.enums;

/**
 * Represents the type of the packet
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public enum OpCodesEnum implements TransfileEnums
{
    RRQ((byte) 0, RequestsEnum.class), INF((byte) 1, InformationsEnum.class), ERR(
            (byte) 2, ErrorsEnum.class), DATA((byte) 3, DatasEnum.class);

    /**
     * Constructor
     * @param b the code representing the correct type
     * @param eCode the enum class corresponding
     */
    OpCodesEnum(byte b, Class<? extends TransfileEnums> eCode)
    {
        opCode = b;
        enumOfCode = eCode;
    }

    /**
     * the byte representing the packet
     */
    private final byte opCode;

    /**
     * The correct enum class of the packet
     */
    private final Class<? extends TransfileEnums> enumOfCode;

    @Override
    public int getLength()
    {
        return Byte.SIZE / 8;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[] { opCode };
    }

    public Class<? extends TransfileEnums> getEnumOfCode()
    {
        return enumOfCode;
    }
}
