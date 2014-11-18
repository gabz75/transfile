package fr.upemlv.transfile.packets;

import fr.upemlv.transfile.enums.OpCodesEnum;
import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class extends OpCodes and represent the format code of the
 * RequestsPackets
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public abstract class Requests extends OpCodes
{
    /**
     * The format packet representing the request type
     */
    private final RequestsEnum requestCode;

    protected Requests(RequestsEnum rC)
    {
        super(OpCodesEnum.RRQ);
        requestCode = rC;
    }

    /**
     * @return the request Code
     */
    public RequestsEnum getRequestCode()
    {
        return requestCode;
    }

    @Override
    public byte[] buildDatas()
    {
        return Utils.mergeByteArray(super.buildDatas(), requestCode.getBytes());
    }
}
