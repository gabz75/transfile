package fr.upemlv.transfile.utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * This class is design to log some data into a file.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Logger
{
    /**
     * The log file
     */
    private RandomAccessFile file;

    /**
     * The FileChannel associated to the log file
     */
    private FileChannel fc;

    /**
     * The Writer associated to the log file
     */
    private Writer writer;

    /**
     * The PrintWriter associated to the log file
     */
    private PrintWriter printer;

    public Logger(String nameFile)
    {
        try {
            file = new RandomAccessFile(nameFile, "rw");
            fc = file.getChannel();
            writer = new FileWriter(file.getFD());
            printer = new PrintWriter(writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Log the content to the current log file
     * 
     * @param content
     *            the content to log
     */
    public void print(String content)
    {
        content = content + "\n\r";
        ByteBuffer bb = ByteBuffer.wrap(content.getBytes(Charset
                .forName("UTF-8")));

        try {
            while (bb.hasRemaining())
                fc.write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the writer of the log file
     */
    public PrintWriter getWriter()
    {
        return printer;
    }
}
