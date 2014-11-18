package fr.upemlv.transfile.exceptions;

/**
 * 
 * Represents a TransfileException
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
@SuppressWarnings("serial")
public abstract class TransfileException extends Exception
{
    /**
     * The error message
     */
    private final String message;

    /**
     * Constructor
     * @param message the error message
     */
    protected TransfileException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
