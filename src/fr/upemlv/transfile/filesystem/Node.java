package fr.upemlv.transfile.filesystem;

import java.io.File;

import fr.upemlv.transfile.exceptions.TransfileException;
import fr.upemlv.transfile.packets.informations.InfoLs;

public interface Node
{

    public File getFile();

    /**
     * Return the child of the node which with the name corresponding to the
     * parameter
     * 
     * @param name
     * @return the child
     */
    public Node getChildWithName(String name) throws TransfileException;

    /**
     * Return the parent directory
     * 
     * @return the parent
     */
    public Node getParent();

    /**
     * Return the root directory
     * 
     * @return the root
     */
    public Node getRoot();

    /**
     * Return the number of file and directory in the current directory
     * 
     * @return the number of children
     */
    public int getChildCount();

    /**
     * Return if a child exists
     * 
     * @param name
     * @return true if the file exist, otherwise false
     */
    public boolean isExist(String name);

    /**
     * Return if the current directory is a file or not
     * 
     * @return true if the current directory is a fire otherwise false
     */
    public boolean isFile();

    /**
     * Return an the class Ls filled with the information of the current
     * directory and their children
     * 
     * @return the Ls Structure
     */
    public InfoLs getLS();

    /**
     * Return the unique identifier for that file
     * 
     * @return the unique identifier of the current file
     */
    public int getId();

}
