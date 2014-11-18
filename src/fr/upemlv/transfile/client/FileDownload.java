package fr.upemlv.transfile.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import fr.upemlv.transfile.settings.Settings;

/**
 * 
 * Represents a downloading file
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class FileDownload
{

    /**
     * the file id, give by the server
     */
    private final int id;
    
    /**
     * The server's Path of the file 
     */
    private final String name;
    
    /**
     *  The RandomAccessFile
     */
    private final RandomAccessFile file;
    
    /**
     * An array of boolean. Its size is equals to the total fragments number of the file
     * and is initialized at false. 
     * 
     * 	0 < i < n, packets[i] = true means that the i fragment has already been written
     * 	in the file. False otherwise.  
     *   
     */
    private boolean[] packets;
    
    /**
     *	The total fragments number of the file. 
     */
    private int size;
    
    
    /**
     * The FileChannel 
     */
    private final FileChannel fileChannel;
    
    
    /**
     * The Download SocketChannel
     */
    private final SocketChannel channel;
    
    /**
     * Constructor
     * @param id the file id
     * @param fileName the file Path
     * @param channel The Download SocketChannel
     * @throws FileNotFoundException
     */
    public FileDownload(int id, String fileName, SocketChannel channel) throws FileNotFoundException
    {
        this.id = id;
        this.name = fileName;
        this.channel = channel;
        int delimiter = fileName.lastIndexOf(File.separatorChar);
        String path = fileName.substring(0, delimiter);
        if (!path.equals("")) {
            File folder = new File(path.substring(1));
            folder.mkdirs();
        }
        File newFile = new File(fileName.substring(1));
        if(newFile.exists()) {
        	newFile.delete();
        }
        file = new RandomAccessFile(newFile, "rw");
        fileChannel = file.getChannel();
    }

    /**
     * Sets the correct size and initialize the boolean array to false;
     * @param size the size
     */
    public void setSize(int size)
    {
        this.size = size;
        packets = new boolean[size];
        for (int i = 0; i < size; i++) {
            packets[i] = false;
        }
    }

    /**
     * Gets the file id
     * @return the file id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the RandomAccessFile
     * @return the RandomAccessFile
     */
    public RandomAccessFile getFile()
    {
        return file;
    }

    /**
     * Gets the boolean arrray
     * @return the boolean array
     */
    public boolean[] getPackets()
    {
        return packets;
    }

    /**
     * Adds the value given in parameter in the boolean array
     * @param i the number
     */
    private void add(int i)
    {
        packets[i] = true;
    }

    /**
     * Gets all the missing fragments numbers
     * @return a list of Integer representing all the missing fragments numbers.
     */
    public List<Integer> getPacketsMissing()
    {
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            if (!packets[i])
                list.add(i + 1);
        }
        return list;
    }

    /**
     * Writes the datas at the number's position
     * @param datas the datas
     * @param number the fragment number
     * @throws IOException
     */
    public void write(byte[] datas, int number) throws IOException
    {
        if (packets[number - 1])
            return;
        add(number - 1);
        fileChannel.write(ByteBuffer.wrap(datas), (number - 1)
                * Settings.DATA_SIZE);
    }

    /**
     * Gets the progression of the download
     * @return the progress
     */
    public double getProgress()
    {
        double d = (size - getPacketsMissing().size()) * 100 / size;
        return d;
    }

    @Override
    public String toString()
    {
        return "ID : " + id + " -- File Name : " + name + " -- Progress : "
                + getProgress() + " %.\n";
    }

    /**
     * Closes the file and the FileChannel
     * @throws IOException
     */
    public void closeFileChannel() throws IOException
    {
    	fileChannel.close();
        file.close();
    }

    /**
     * Gets the file name
     * @return the file name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Closes the download channel
     * @throws IOException
     */
    public void closeDownloadChannel() throws IOException {
    	channel.close();
    }

}
