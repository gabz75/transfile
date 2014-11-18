package fr.upemlv.transfile.exceptions;

/**
 * 
 * Represents an NotExistentResourceException
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
@SuppressWarnings("serial")
public class NotExistentResourceException extends TransfileException
{

    /**
     * Constructor
     * @param message the error message
     */
    public NotExistentResourceException(String message)
    {
        super(message);
    }
}
