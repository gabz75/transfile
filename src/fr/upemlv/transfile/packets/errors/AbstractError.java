package fr.upemlv.transfile.packets.errors;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.packets.Errors;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

public abstract class AbstractError extends Errors
{
    protected final String message;

    protected AbstractError(String message, ErrorsEnum eC)
    {
        super(eC);
        this.message = message;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] header = super.buildDatas();

        return Utils.mergeByteArrays(header,
                message.getBytes(Settings.ENCODING), new byte[] { 0 });
    }
}
