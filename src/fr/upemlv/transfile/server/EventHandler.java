package fr.upemlv.transfile.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Each attachment in the server must implements this interface and define the
 * two methods read and write
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public interface EventHandler
{
    /**
     * This method is called when the key of a socketChannel allow write
     * operation. Define your writing treatment in this method
     * 
     * @param key
     *            SelectionKey of the channel
     * @throws IOException
     */
    public void processWrite(SelectionKey key) throws IOException;

    /**
     * This method is called when the key of a socketChannel allow read
     * operation. Define your reading treatment in this method
     * 
     * @param key
     *            the SelectionKey of the channel
     * @throws IOException
     */
    public void processRead(SelectionKey key) throws IOException;
}
