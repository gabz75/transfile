package fr.upemlv.transfile.exceptions;

/**
 * 
 * Represents an IllegalRequestException
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
@SuppressWarnings("serial")
public class IllegalRequestException extends TransfileException
{

    /**
     * Constructor
     * @param message the error message
     */
    public IllegalRequestException(String message)
    {
        super(message);
    }
}
