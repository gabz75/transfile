package fr.upemlv.transfile.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Locale;
import java.util.Scanner;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.exceptions.IllegalRequestException;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.packets.requests.RequestFactory;
import fr.upemlv.transfile.packets.requests.RqExit;
import fr.upemlv.transfile.packets.requests.RqId;

/**
 * Represents the Client's Send Thread
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class ClientSend extends Thread
{

    /**
     * The main Socket channel
     */
    private final SocketChannel channel;
    
    /**
     * The writer buffer 
     */
    private ByteBuffer writer;
    
    /**
     *The instance of the receive thread 
     */
    private ClientReceive receive;
    
    /**
     * The scanner
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructor
     * @param channel the main channel
     * @param receive the receive thread
     */
    public ClientSend(SocketChannel channel, ClientReceive receive)
    {
        this.channel = channel;
        this.receive = receive;
    }

    @Override
    public void run()
    {
        try {
            sendRequestId();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        try {
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                try {
                    analyseMessage(msg);
                } catch (IllegalRequestException e) {
                    System.err.println(e.getMessage());
                    receive.prompt();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    receive.prompt();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    receive.prompt();
                }
            }
        }
        catch(IllegalStateException e) {
        	
        }finally {
            try {
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Sends an id request to the server
     * @throws IOException
     */
    private void sendRequestId() throws IOException
    {
        writer = ByteBuffer.wrap(new RqId().buildDatas());
        while (writer.hasRemaining()) {
            channel.write(writer);
        }
    }

    /**
     * Checks the message given in parameter.
     * If the command matches with a RequestEnum then a Request is sent,
     * throws an IllegalRequestException otherwise.
     * @param msg the message
     * @throws IllegalRequestException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void analyseMessage(String msg) throws IllegalRequestException,
            IOException, IllegalArgumentException
    {
        if (msg.equals("")) {
        	receive.prompt();
        	return;
        }
        if(msg.equals("?")) {
        	helpUser();
        	receive.prompt();
        	return;
        }
        
        if ("status".equals(msg.toLowerCase())) {
            System.out.println(receive.statusToString());
            receive.prompt();
            return;
        }
        
        if ("stop".equals(msg.toLowerCase())) {
            scanner.close();
            channel.close();
            return;
        }
        String[] fullRequest = msg.split(" ");
        Requests request = null;
        if(fullRequest[0].equals("")) throw new IllegalRequestException("Your command must not starts with a SPACE");
        for (RequestsEnum req : RequestsEnum.values()) {
            if (req.getName()
                    .equals(fullRequest[0].toLowerCase(Locale.ENGLISH))) {
                request = RequestFactory.createRequest(req, fullRequest);
                sendRequest(request);
            }
        }
        if (request == null)
            throw new IllegalRequestException("The request is not valid.");
    }

    private void helpUser() {
    	System.out.println(" ----------   HELP -----------");
    	System.out.println(" You will find below the list of all the requests available : ");
    	System.out.println(" - \'LS\'      : prints all the files upon the server\'s current directory");
    	System.out.println(" - \'CD XXX\'  : Changes the current server directory to XXX");
    	System.out.println(" - \'GET XXX\' : downloads the file named XXX");
    	System.out.println(" - \'STATUS\'  : prints all the current downloads with their fileID and their progress");
    	System.out.println(" - \'KILL 0\'  : kills the process number 0");
    	System.out.println(" - \'?\'       : prints the help");
    	System.out.println(" - \'STOP\'    : closes the command scanner on System.in, and waits until all the current downloads are over");
    	System.out.println(" - \'EXIT\'    : leaves the program and interrupts all the current downloads");
	}

	/**
     * Sends the request to the server
     * @param request the request
     * @throws IOException
     */
    private void sendRequest(Requests request) throws IOException
    {
        if (request instanceof RqExit) {
            scanner.close();
        }
        
        writer = ByteBuffer.wrap(request.buildDatas());
        while (writer.hasRemaining()) {
            channel.write(writer);
        }
    }

}
