package fr.upemlv.transfile.packets;

import fr.upemlv.transfile.enums.OpCodesEnum;

/**
 * The Operand code is always present in the header of Transfile datagram. This
 * class defines its implementation.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public abstract class OpCodes implements TransfilePackets
{
    /**
     * The operandCode of a packet
     */
    private final OpCodesEnum opCode;

    protected OpCodes(OpCodesEnum opC)
    {
        opCode = opC;
    }

    /**
     * @return the current operand code
     */
    public OpCodesEnum getOpCode()
    {
        return opCode;
    }

    @Override
    public byte[] buildDatas()
    {
        return opCode.getBytes();
    }
}
