package fr.upemlv.transfile.packets.errors;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

public class AccessDenied extends AbstractError
{
    private final static String message = "Access denied.";

    public AccessDenied()
    {
        super(message, ErrorsEnum.ACCESS_DENIED);
    }

    public static AccessDenied decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new AccessDenied();
    }

    @Override
    public String toString()
    {
        return message;
    }
}
