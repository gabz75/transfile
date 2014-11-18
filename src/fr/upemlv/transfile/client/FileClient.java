package fr.upemlv.transfile.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import fr.upemlv.transfile.settings.Settings;

/**
 * 
 * Represents a client, by launching the send and receive threads.
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class FileClient
{

    /**
     * The SocketChannel between the client and the server
     * where the main requests and answers are transferred.
     */
    private final SocketChannel channel;

    /**
     * Constructor
     * @param address the distant address
     * @param port the distant port
     * @throws IOException
     */
    public FileClient(String address, int port) throws IOException
    {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(address, port));
        Settings.serverAddress = address;
        Settings.port = port;
        launch();
    }

    public static void main(String[] args) throws IOException
    {
        new FileClient(args[0], Integer.parseInt(args[1]));
    }

    /**
     * Creates and launches the two threads.
     * @throws IOException
     */
    private void launch() throws IOException
    {
        ClientReceive receive = new ClientReceive(channel);
        ClientSend send = new ClientSend(channel, receive);
        send.start();
        receive.start();
    }
}
