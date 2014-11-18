package fr.upemlv.transfile.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map.Entry;

import fr.upemlv.transfile.filesystem.FileNode;
import fr.upemlv.transfile.settings.Settings;

/**
 * This class is design to write stats in a file.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 * 
 */
public class Statistics
{
    /**
     * The singleton instance of statistics
     */
    private volatile static Statistics statistics;

    /**
     * The map of all file downloaded by ID
     */
    private final HashMap<Integer, File> fileIdMap;

    /**
     * The map of all data transmitted by file.
     */
    private final HashMap<Integer, Integer> fileStatsMap;

    /**
     * The total length of bytes transmitted
     */
    private double transmission;

    /**
     * The total byte transmitted by the server.
     */
    private double receive;

    private Statistics()
    {
        this.fileStatsMap = new HashMap<Integer, Integer>();
        this.fileIdMap = new HashMap<Integer, File>();
        this.transmission = 0;
        this.receive = 0;
    }

    /**
     * Return the singleton instance
     * 
     * @return the instance of Statistics
     */
    public static Statistics getInstance()
    {
        if (statistics == null) {
            synchronized (Statistics.class) {
                if (statistics == null) {
                    statistics = new Statistics();
                }
            }
        }
        return statistics;
    }

    /**
     * Return the total transmission statistics measured in bytes
     * 
     * @return the total transmission statistics measured in bytes
     */
    public double getTransmissionStats()
    {
        return transmission;
    }

    /**
     * Add to the total transmission measure the given value.
     * 
     * @param transmission
     *            the byte to add to the total count
     */
    public void addTransmissionStats(double transmission)
    {
        this.transmission += transmission;
    }

    /**
     * Return the total reception statistics measured in bytes
     * 
     * @return the total reception statistics measured in bytes
     */
    public double getReceiveStats()
    {
        return receive;
    }

    /**
     * Add to the total reception measure the given value.
     * 
     * @param receives
     *            the byte to add to the total count
     */
    public void addReceiveStats(double receives)
    {
        this.receive += receives;
    }

    /**
     * Add to the file ID the value of bytes transmitted
     * 
     * @param id
     * @param i
     */
    public void addFileStats(int id, int i)
    {
        if (fileStatsMap.get(id) == null) {
            fileStatsMap.put(id, 0);
        }

        int stats = fileStatsMap.get(id);
        fileStatsMap.put(id, stats + i);
    }

    public void addFileStats(File f, int i)
    {
        int id = FileNode.getIdOfFile(f);
        fileIdMap.put(id, f);

        addFileStats(id, i);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Statistics of server : \n");

        sb.append("Total datas transferred in transmission : " + transmission
                / 1000 + " Kbytes.\n");
        sb.append("Total datas transferred in reception : " + receive / 1000
                + " Kbytes.\n");
        sb.append("\n");
        sb.append("------------------------------------------------------------------");
        sb.append("\n");

        for (Entry<Integer, Integer> entry : fileStatsMap.entrySet()) {
            File file = fileIdMap.get(entry.getKey());
            sb.append(file.getAbsolutePath() + " downloaded "
                    + entry.getValue().intValue() + " times.\n");
        }

        return sb.toString();
    }

    public void save()
    {
        try {
            FileOutputStream fos = new FileOutputStream(new File(
                    Settings.PATH_STATS));
            FileChannel fc = fos.getChannel();

            byte[] content = toString().getBytes(Settings.ENCODING);
            ByteBuffer bb = ByteBuffer.wrap(content);

            while (bb.hasRemaining()) {
                fc.write(bb);
            }

            fc.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
