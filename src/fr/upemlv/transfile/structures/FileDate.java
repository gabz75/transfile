package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.utils.Conversion;

/**
 * 
 * Represents a file date
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class FileDate implements TransfilePackets
{
    /**
     * The year
     */
    private final int year;

    /**
     * the month
     */
    private final int month;

    /**
     * the day
     */
    private final int day;

    /**
     * The size in bytes representing the file date
     */
    private final static int SIZE = Integer.SIZE / 8;

    /**
     * Constructor
     * @param y the year
     * @param m the month
     * @param d the day
     */
    public FileDate(int y, int m, int d)
    {
        year = y;
        month = m;
        day = d;
    }

    /**
     * Gets the length in bytes
     * @return SIZE
     */
    public int getLength()
    {
        return SIZE;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] m = Conversion.intToByte(month);
        byte[] d = Conversion.intToByte(day);
        byte[] y = Conversion.intToByte(year);
        ByteBuffer bb = ByteBuffer.allocate(SIZE);

        bb.put(d[0]);
        bb.put(m[0]);
        bb.put(y[0]);
        bb.put(y[1]);

        return bb.array();
    }

    /**
     * Decodes a FileDate from a ByteBuffer given in argument
     * @param bbr the ByteBuffer
     * @return a new Instance of FileDate
     * @throws UncompletedPackageException
     */
    public static FileDate decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int day = bbr.get();
        int month = bbr.get();
        byte y1 = bbr.get();
        byte y2 = bbr.get();

        ByteBuffer tmp = ByteBuffer.allocate(4);
        tmp.put((byte) 0);
        tmp.put((byte) 0);
        tmp.put(y2);
        tmp.put(y1);
        tmp.clear();
        int year = tmp.getInt();

        return new FileDate(year, month, day);
    }

    @Override
    public String toString()
    {
        String m = (this.month < 10 ) ? "0" + this.month : "" + this.month;
        String d = (this.day < 10 ) ? "0" + this.day : "" + this.day;
        
        return year + "/" + m + "/" + d;
    }
}
