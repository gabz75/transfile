package fr.upemlv.transfile.packets;

import fr.upemlv.transfile.enums.InformationsEnum;
import fr.upemlv.transfile.enums.OpCodesEnum;
import fr.upemlv.transfile.utils.Utils;

/**
 * This class extends OpCodes and represent the format code of the
 * InformationsPackets
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Informations extends OpCodes
{
    /**
     * The format code representing the type of information.
     */
    private final InformationsEnum informationCode;

    public Informations(InformationsEnum iCode)
    {
        super(OpCodesEnum.INF);
        informationCode = iCode;
    }

    /**
     * @return the information code
     */
    public InformationsEnum getInformationCode()
    {
        return informationCode;
    }

    @Override
    public byte[] buildDatas()
    {
        return Utils.mergeByteArray(super.buildDatas(),
                informationCode.getBytes());
    }
}
