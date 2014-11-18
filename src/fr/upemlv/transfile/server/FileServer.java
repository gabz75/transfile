package fr.upemlv.transfile.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import fr.upemlv.transfile.exceptions.IllegalRequestException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.packets.TransfileDecoder;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.data.AbstractData;
import fr.upemlv.transfile.packets.errors.BadRequest;
import fr.upemlv.transfile.packets.informations.InfoId;
import fr.upemlv.transfile.packets.requests.RqDownload;
import fr.upemlv.transfile.settings.Settings;

/**
 * This class specify the implementation of a server based on the Transfile
 * protocol
 * 
 * This class contains two attachment implementing the interface EventHandler.
 * 
 * RequestCommandHandler is used to manage the command requested sent by the
 * client. DataHandler is used to manage the download requested by the client
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class FileServer
{
    /**
     * The server is configured in non blocking mode, and use this selector
     */
    private final Selector selector;

    /**
     * HashMap to store each client, register the clients with their ID and as
     * value the selectionKey of the channel We only register the SocketChannel
     * used for the command.
     */
    public static HashMap<Integer, SelectionKey> clientMap = new HashMap<Integer, SelectionKey>();

    /**
     * The root path of the fileSystem
     */
    private String root;

    public FileServer() throws IOException
    {
        selector = Selector.open();
    }

    /**
     * Starts the server on the given port and the given root path.
     * 
     * This method register the ServerSocketChannel on AcceptOperands, and wait
     * for connection. Browse all the key returns by the selector and forward to
     * the equivalent method.
     * 
     * 
     * @param port
     *            the port where the server is binded
     * @param root
     *            the root path of the fileSystem
     * @throws IOException
     */
    public void launch(int port, String root, String address)
            throws IOException
    {
        this.root = root;
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(address, port));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        final Thread mainThread = Thread.currentThread();
        new Thread(new Runnable() {

            @Override
            public void run()
            {
                Scanner scanner = new Scanner(System.in);

                try {
                    while (scanner.hasNextLine()) {
                        String cmd = scanner.nextLine();
                        if (cmd.equals("stats")) {
                            Statistics.getInstance().save();
                            System.out.println(Statistics.getInstance()
                                    .toString());
                        }
                        if (cmd.equals("exit")) {
                            break;
                        }
                    }
                } finally {
                    scanner.close();
                    mainThread.interrupt();
                }
            }
        }).start();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Set<SelectionKey> readyKeys = selector.selectedKeys();

                for (SelectionKey key : readyKeys) {
                    try {
                        if (key.isValid() && key.isAcceptable()) {
                            treatAccept(key);
                        }
                        if (key.isValid() && key.isWritable()) {
                            treatWrite(key);
                        }
                        if (key.isValid() && key.isReadable()) {
                            treatRead(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                        terminateKey(key);
                    }
                }

                readyKeys.clear();
            }
        } finally {
            ssc.close();
            selector.close();
        }
    }

    /**
     * Treatment when a the selector detects an Accept operand on the
     * ServerSocketChannel. It will accept the new SocketChannel and attach to
     * it an instance of RequestCommandHandler. Each SocketChannel are
     * configured on non blocking mode, and registered on READ operand to the
     * selectors.
     * 
     * @param key
     *            the current SelectionKey of the ServerSocketChannel
     * @throws IOException
     */
    private void treatAccept(SelectionKey key) throws IOException
    {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        SelectionKey clientKey = sc.register(selector, SelectionKey.OP_READ,
                new RequestCommandHandler(root));

        System.out
                .println("New connection accepted for : "
                        + sc.socket().getRemoteSocketAddress() + ", key : "
                        + clientKey);
    }

    /**
     * Get the attachment of the key and call the processWrite method, defined
     * by the Interface EventHandler.
     * 
     * @param key
     *            the current SelectionKey
     * @throws IOException
     */
    private void treatWrite(SelectionKey key) throws IOException
    {
        EventHandler attach = (EventHandler) key.attachment();
        attach.processWrite(key);
    }

    /**
     * Get the attachment of the key and call the processRead method, defined by
     * the Interface EventHandler.
     * 
     * @param key
     *            the current SelectionKey
     * @throws IOException
     */
    private void treatRead(SelectionKey key) throws IOException
    {
        EventHandler attach = (EventHandler) key.attachment();
        attach.processRead(key);
    }

    /**
     * Remove the current operand with the exclusive or, works only if the
     * operand is already in the set
     * 
     * If there the client has shutdown the channel and the read will see -1 and
     * remove read operand, in that case no more key remains and we must
     * terminate the key
     */
    private static void removeFromKey(SelectionKey key, int op)
    {
        int status = key.interestOps() ^ op;
        if (status == 0) {
            terminateKey(key);
        } else {
            key.interestOps(status);
        }
    }

    /**
     * Terminate a SelectionKey, by canceling this one of the selector, and
     * remove attachment. Then it will close the channel associated to the
     * SelectionKey
     * 
     * @param key
     *            the SelectionKey that will be cancel
     */
    public static void terminateKey(SelectionKey key)
    {
        key.cancel();
        key.attach(null);

        try {
            key.channel().close();
        } catch (IOException e) {
            // do nothing;
        }
    }

    /**
     * Create a new Server and launch it with the given parameters
     * 
     * @param args
     * @throws NumberFormatException
     * @throws IOException
     */
    public static void main(String[] args) throws NumberFormatException,
            IOException
    {
        int port = 0;
        String address = "127.0.0.1";
        String path = "";

        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-a")) {
                    address = args[i + 1];
                }
                if (args[i].equals("-d")) {
                    path = args[i + 1];
                }
                if (args[i].equals("-p")) {
                    port = Integer.parseInt(args[i + 1]);
                }
            }
        }

        if (port != 0 && !path.equals("")) {
            new FileServer().launch(port, path, address);
        } else {
            System.out
                    .println("> arguments must be : -p [port] -d [path of the file system] -a (optionnal) [address]");
        }
    }

    /**
     * 
     * This class define an Attachment to manage the Request command sent by the
     * client, it will treat the command and send response.
     * 
     * If the client send an Information packet containing his ID, it will swap
     * this attachment with an instance of the DataHandler classes defined
     * above.
     * 
     */
    private static class RequestCommandHandler implements EventHandler
    {
        /**
         * The decoder is used to analyze the data receipt, it browse a
         * ByteBuffer and returns an instance of TransfilePackets
         */
        private final TransfileDecoder decoder;

        /**
         * The responder is used to treat a request packet, and return the
         * adequate Information or Errors packets
         */
        private final TransfileRequestResponder responder;

        /**
         * The ByteBuffer used for the reading operations
         */
        private final ByteBuffer bbr = ByteBuffer.allocate(Settings.BUFF_SIZE);

        /**
         * The ByteBuffer used for the reading operations
         */
        private ByteBuffer bbw = ByteBuffer.allocate(Settings.BUFF_SIZE);

        private RequestCommandHandler(String root)
        {
            decoder = new TransfileDecoder(false, "-server");
            responder = new TransfileRequestResponder(root);
        }

        @Override
        public void processRead(SelectionKey key) throws IOException
        {
            SocketChannel sc = (SocketChannel) key.channel();

            bbr.clear();
            int length = sc.read(bbr);

            if (length > 0) {
                Statistics.getInstance().addReceiveStats(length);

                TransfilePackets packets = null;
                Requests request = null;
                try {
                    packets = decoder.decode(bbr);
                } catch (IllegalRequestException e) {
                    bbw.put(new BadRequest().buildDatas());
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    return;
                }

                if (packets == null) {
                    return;
                }

                if (packets instanceof InfoId) {
                    InfoId infoId = (InfoId) packets;
                    key.attach(new DataHandler(infoId.getId()));
                    return;
                }

                try {
                    request = (Requests) packets;
                } catch (ClassCastException e) {
                    /**
                     * it means that we've read an other thing that a request
                     * packets, and we can drop it
                     */
                    return;
                }

                TransfilePackets response = responder.getResponse(request, key);
                if (response != null) {
                    byte[] data = response.buildDatas();
                    if (data.length > Settings.BUFF_SIZE) {
                        bbw = ByteBuffer.allocate(data.length);
                    }

                    bbw.put(response.buildDatas());
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
            }

            if (length == -1) {
                removeFromKey(key, SelectionKey.OP_READ);
            }
        }

        @Override
        public void processWrite(SelectionKey key) throws IOException
        {
            SocketChannel sc = (SocketChannel) key.channel();

            bbw.flip();
            int written = sc.write(bbw);
            bbw.compact();

            Statistics.getInstance().addTransmissionStats(written);

            if (bbw.position() == 0) {
                removeFromKey(key, SelectionKey.OP_WRITE);
            }
        }
    }

    /**
     * 
     * This class define an Attachment to manage the Data to send to the
     * clients. It will treat only RqDownload packet in reception. Any other
     * packet will be drop.
     * 
     */
    private static class DataHandler implements EventHandler
    {
        /**
         * The currentRequestDownload
         */
        private RqDownload download;

        /**
         * The fileSplitter is used to split the file wanted by the client
         */
        private FileSplitter fileSplitter;

        /**
         * The decoder is used to analyze the data receipt, it browse a
         * ByteBuffer and returns an instance of TransfilePackets
         */
        private final TransfileDecoder decoder;

        /**
         * Correspond to the key of the Command SocketChannel
         */
        private final SelectionKey rqChannelKey;

        /**
         * Correspond to the attachment of the Command SocketChannel
         */
        private final RequestCommandHandler attachRqChannel;

        /**
         * The ID of the client, can be used to retrieve the SelectionKey of the
         * Command SocketChannel
         */
        private final int idClient;

        /**
         * The data to send
         */
        private AbstractData data;

        /**
         * The ByteBuffer used of reading operations
         */
        private ByteBuffer bbw = ByteBuffer.allocate(Settings.BUFF_SIZE);

        /**
         * The ByteBuffer used of writing operations
         */
        private ByteBuffer bbr = ByteBuffer.allocate(Settings.BUFF_SIZE);

        public DataHandler(int id)
        {
            this.idClient = id;
            this.decoder = new TransfileDecoder(false, "server-data");
            this.rqChannelKey = clientMap.get(idClient);
            this.attachRqChannel = (RequestCommandHandler) rqChannelKey
                    .attachment();
        }

        @Override
        public void processRead(SelectionKey key) throws IOException
        {
            SocketChannel sc = (SocketChannel) key.channel();

            bbr.clear();
            int length = sc.read(bbr);

            if (length > 0) {
                Statistics.getInstance().addReceiveStats(length);

                TransfilePackets packets = null;
                try {
                    packets = decoder.decode(bbr);
                } catch (IllegalRequestException e) {
                    /**
                     * If the paquet are undecodable we drop the packet
                     */
                    return;
                }

                if (packets != null && packets instanceof RqDownload) {
                    RqDownload download = (RqDownload) packets;
                    setCurrentDownload(download);
                    attachRqChannel.responder.addDownloadKey(download.getId(),
                            key);
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

                    if (download.isLast()) {
                        removeFromKey(key, SelectionKey.OP_READ);
                    }
                }
            }
        }

        @Override
        public void processWrite(SelectionKey key) throws IOException
        {
            SocketChannel sc = (SocketChannel) key.channel();

            if (data == null) {
                attachRqChannel.responder.removeDownloadKey(download.getId());
                removeFromKey(key, SelectionKey.OP_WRITE);
                return;
            }

            bbw.flip();
            int written = sc.write(bbw);
            bbw.compact();

            Statistics.getInstance().addReceiveStats(written);

            if (bbw.position() == 0) {
                data = fileSplitter.getData();
                if (data != null) {
                    bbw.put(data.buildDatas());
                }
            }
        }

        /**
         * Browse a RqDownload packet and set the current data to split and send
         * 
         * Actually it only affect the data fields, in the case of an
         * implementation of the UDP TransfileProtocol, it will be necessary to
         * store the data in a stack to manage severals RqDownload
         * 
         * @param dl
         *            a request Download
         */
        private void setCurrentDownload(RqDownload dl)
        {
            download = dl;
            File f = attachRqChannel.responder.fetchFileById(dl.getId());
            try {
                fileSplitter = new FileSplitter(f, download);
                data = fileSplitter.getData();
                bbw.put(data.buildDatas());
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
