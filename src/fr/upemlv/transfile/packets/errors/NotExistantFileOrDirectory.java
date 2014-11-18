package fr.upemlv.transfile.packets.errors;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

public class NotExistantFileOrDirectory extends AbstractError
{
    private final static String message = "The resource that you ask can't be found.";

    public NotExistantFileOrDirectory()
    {
        super(message, ErrorsEnum.NOT_EXITANT_FILE_DIRECTORY);
    }

    public static NotExistantFileOrDirectory decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new NotExistantFileOrDirectory();
    }

    @Override
    public String toString()
    {
        return message;
    }
}
