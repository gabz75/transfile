package fr.upemlv.transfile.exceptions;

/**
 * Represents an AlreadyExistingRessource Exception
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class AlreadyExistingRessource extends TransfileException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message the error message
     */
    public AlreadyExistingRessource(String message)
    {
        super(message);
    }

}
