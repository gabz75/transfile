package fr.upemlv.transfile.settings;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Represents all the settings/constants of the application TransFile.
 * Those are all public static final fields to be red easily anywhere.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class Settings
{
    /**
     * The Charset used by TransFile
     */
    public static Charset ENCODING = Charset.forName("ASCII");

    /**
     * The server's Address
     */
    public static String serverAddress = "";

    /**
     * The port used by the server
     */
    public static int port = 0;

    
    /**
     * The fragment number where a file starts 
     */
    public static final int FILE_START = 1;

    /**
     * represents the end of the file
     */
    public static final int FILE_END = 0;

    /**
     * The maximum download threads number running at the same time 
     */
    public static final int MAX_THREAD = 20;

    /**
     * The size in Bytes of the datas
     */
    public static final int DATA_SIZE = 1400;
    
    /**
     * 
     */
    public static final int DATA_PACKET_SIZE = 18;
    
    /**
     * A value representing the last fragment
     */
    public static final int LAST_FRAGMENT = 1;
    
    
    /**
     * A value representing that more fragments are required
     */
    public static final int MORE_FRAGMENT = 0;
    
    /**
     * The correct size for all the readers buffer.
     */
    public static final int BUFF_SIZE = 1400 + DATA_PACKET_SIZE;

    /**
     * The Path of the statistics file
     */
    public static final String PATH_STATS = "server-statistics";

    private static final File f = new File(".");

    /**
     * The Server root path
     */
    public static final String PATH = f.getAbsolutePath();

}
