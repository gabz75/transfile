package fr.upemlv.transfile.packets;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.enums.OpCodesEnum;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class extends OpCodes and represent the format code of the
 * ErrorsPackets
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Errors extends OpCodes
{
    /**
     * The format code representing the type of error
     */
    private final ErrorsEnum errorCode;

    protected Errors(ErrorsEnum eC)
    {
        super(OpCodesEnum.ERR);
        errorCode = eC;
    }

    /**
     * @return the error code
     */
    public ErrorsEnum getErrorCode()
    {
        return errorCode;
    }

    @Override
    public byte[] buildDatas()
    {
        return Utils.mergeByteArray(super.buildDatas(), errorCode.getBytes());
    }
}
