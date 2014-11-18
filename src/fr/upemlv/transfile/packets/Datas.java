package fr.upemlv.transfile.packets;

import fr.upemlv.transfile.enums.OpCodesEnum;
import fr.upemlv.transfile.enums.DatasEnum;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class extend OpCodes and represent the format code of the Datas packets.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Datas extends OpCodes
{
    /**
     * The format code containing the protocol code
     */
    private final DatasEnum protocolCodes;

    protected Datas(DatasEnum pE)
    {
        super(OpCodesEnum.DATA);
        protocolCodes = pE;
    }

    /**
     * @return the protocol code
     */
    public DatasEnum getProtocolCode()
    {
        return protocolCodes;
    }

    @Override
    public byte[] buildDatas()
    {
        return Utils.mergeByteArray(super.buildDatas(),
                protocolCodes.getBytes());
    }

}
