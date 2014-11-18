package fr.upemlv.transfile.exceptions;

/**
 * 
 * Represents an UncompletedPackageException
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
@SuppressWarnings("serial")
public class UncompletedPackageException extends TransfileException
{

    /**
     * Constructor
     * @param message the error message
     */
    public UncompletedPackageException(String message)
    {
        super(message);
    }
}