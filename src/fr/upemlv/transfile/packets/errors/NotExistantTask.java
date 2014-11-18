package fr.upemlv.transfile.packets.errors;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.ErrorsEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.settings.Settings;
import fr.upemlv.transfile.utils.Utils;

public class NotExistantTask extends AbstractError
{
    private final static String message = "The task that you ask can't be found.";

    public NotExistantTask()
    {
        super(message, ErrorsEnum.NOT_EXISTANT_TASK);
    }

    public static NotExistantTask decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        new String(Utils.decodeString(bbr), Settings.ENCODING);

        return new NotExistantTask();
    }

    @Override
    public String toString()
    {
        return message;
    }
}