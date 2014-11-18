package fr.upemlv.transfile.packets.errors;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

public class BadRequest extends AbstractError
{
    private final static String message = "Bad Request.";

    public BadRequest()
    {
        super(message, ErrorsEnum.BAD_REQUEST);
    }

    public static BadRequest decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new BadRequest();
    }

    @Override
    public String toString()
    {
        return message;
    }
}
