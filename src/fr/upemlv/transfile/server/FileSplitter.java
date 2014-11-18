package fr.upemlv.transfile.server;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import fr.upemlv.transfile.packets.data.AbstractData;
import fr.upemlv.transfile.packets.data.DataTCP;
import fr.upemlv.transfile.packets.requests.RqDownload;
import fr.upemlv.transfile.settings.Settings;

/**
 * This class is design to get a File and split it in severals fragment.
 * 
 * It will be base on a given File instance, then the method getData, will split
 * the file and return an AbstractData packet corresponding to the asked
 * fragment of the file. Then it will increase the currentFragment field for the
 * next call of getData
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 * 
 */
public class FileSplitter
{
    /**
     * The current file to split
     */
    private final File file;

    /**
     * The current download Request, to know which fragment are expected
     */
    private final RqDownload download;

    /**
     * The total length measured in byte of the file
     */
    private final int totalByte;

    /**
     * The total number of fragment necessary to split the current File
     */
    private final int totalFragment;

    /**
     * The FileChannel to read in the split file
     */
    private final FileChannel fc;

    /**
     * The current number of fragment, this field is incremented during the
     * download
     */
    private int currentFragment;

    /**
     * The last fragment asked by the client, if the value is O, it will
     * correspond to the end of the file
     */
    private int endFragment;

    public FileSplitter(File f, RqDownload d) throws IOException
    {
        this.file = f;
        this.download = d;

        RandomAccessFile raFile = new RandomAccessFile(file, "r");
        fc = raFile.getChannel();

        this.totalByte = (int) fc.size();
        this.totalFragment = getTotalFragment(fc.size());

        this.setStartEndFragment();
    }

    /**
     * Return the total byte of the file
     * 
     * @return the total byte of the file
     */
    public int getTotalByte()
    {
        return totalByte;
    }

    /**
     * 
     * This method is static to be called by the static method
     * getTotalFragmentOfFile above.
     * 
     * It will calculate the total number of fragment necessary to split the
     * file.
     * 
     * @param totalByte
     *            the length measured in bytes
     * @return the total number of fragment
     */
    private static int getTotalFragment(long totalByte)
    {
        int result = (int) totalByte / Settings.DATA_SIZE;
        if (totalByte % Settings.DATA_SIZE > 0) {
            result++;
        }

        if (result == 0) {
            result = 1;
        }

        return result;
    }

    /**
     * Return the total fragment necessary to split the file.
     * 
     * This method will be based on the length of the given file.
     * 
     * @param f
     *            an instance of File
     * @return the total fragment necessary
     */
    public static int getTotalFragmentOfFile(File f)
    {
        return getTotalFragment(f.length());
    }

    /**
     * Set the start and end fragment.
     * 
     * It will check that the start fragment is not superior to the end, and the
     * end fragment is not superior to the totalFragment number. fragment. And
     * also that if the endFragment is 0 it will set to the end of the file.
     * 
     */
    private void setStartEndFragment()
    {
        currentFragment = 1;
        if (download.getStartFragment() >= 1
                || download.getStartFragment() <= this.totalFragment) {
            currentFragment = download.getStartFragment();
        }

        endFragment = download.getEndFragment();
        if (download.getEndFragment() == Settings.FILE_END
                || download.getEndFragment() > this.totalFragment) {
            endFragment = this.totalFragment;
        }
    }

    /**
     * Get the start position to start reading in the file, to compose a
     * Fragment. It is based on the given fragment number.
     * 
     * @param fragNumber
     * @return the start position to read in the file
     */
    private int getStartPosition(int fragNumber)
    {
        if (fragNumber == 1) {
            return 0;
        }

        return (fragNumber - 1) * Settings.DATA_SIZE;
    }

    /**
     * Get the end position to stop reading in the file. Used to compose a
     * Fragment. It is based on the start position previously calculated.
     * 
     * @param startPosition
     * @return the end position to read in the file
     * @throws IOException
     */
    private int getEndPosition(int startPosition) throws IOException
    {
        if (currentFragment == totalFragment) {
            long endPos = fc.size() - getStartPosition(currentFragment);

            return startPosition + (int) endPos;
        } else {
            return startPosition + Settings.DATA_SIZE;
        }
    }

    /**
     * Read in the current File, calculate the start and end position based on
     * the currentFragment and returns the data of this fragment.
     * 
     * The current Fragment is incremented at each end of this functions. If the
     * currentFragment is superior to the total fragment, it will close the
     * fileChannel and return null.
     * 
     * @return the data of the current Fragment
     * @throws IOException
     */
    private byte[] getFragment() throws IOException
    {
        if (currentFragment > endFragment) {
            fc.close();
            return null;
        }

        int startPos = getStartPosition(currentFragment);
        int endPos = getEndPosition(startPos);

        ByteBuffer bb = ByteBuffer.allocate(endPos - startPos);

        while (bb.hasRemaining()) {
            try {
                fc.read(bb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        currentFragment++;

        return bb.array();
    }

    /**
     * Return an instance of Abstract data based on the getFragment method, in
     * other term it will return the DataPackets for 1 fragment of the file.
     * 
     * @throws IOException
     * @return the AbstractData corresponding to the current fragment
     */
    public AbstractData getData() throws IOException
    {
        byte[] datas = getFragment();

        if (datas == null) {
            return null;
        }

        return new DataTCP(this.totalFragment, this.currentFragment - 1,
                this.download.getId(), datas.length, datas);
    }
}