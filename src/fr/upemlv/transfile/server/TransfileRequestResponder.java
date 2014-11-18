package fr.upemlv.transfile.server;

import java.io.File;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;

import fr.upemlv.transfile.exceptions.NotExistentResourceException;
import fr.upemlv.transfile.exceptions.TransfileException;
import fr.upemlv.transfile.filesystem.FileNode;
import fr.upemlv.transfile.filesystem.Node;
import fr.upemlv.transfile.packets.Requests;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.errors.NotExistantFileOrDirectory;
import fr.upemlv.transfile.packets.errors.NotExistantTask;
import fr.upemlv.transfile.packets.informations.InfoCd;
import fr.upemlv.transfile.packets.informations.InfoGet;
import fr.upemlv.transfile.packets.informations.InfoId;
import fr.upemlv.transfile.packets.informations.InfoKill;
import fr.upemlv.transfile.packets.informations.InfoLs;
import fr.upemlv.transfile.packets.requests.RqCd;
import fr.upemlv.transfile.packets.requests.RqGet;
import fr.upemlv.transfile.packets.requests.RqKill;

/**
 * This class is design to treat a Request Packet, and return an Information or
 * Errors Packet.
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 */
public class TransfileRequestResponder
{

    /**
     * A reference of the current FileSystem.
     */
    private Node fileSystem;

    /**
     * An HashMap to store each file asked for a download associated to their ID
     * .
     */
    private static HashMap<Integer, File> taskFileMap = new HashMap<Integer, File>();

    /**
     * An HashMap to store each SelectionKey associated to their file ID.
     */
    private final HashMap<Integer, SelectionKey> downloadsMap;

    /**
     * Used to generate the unique ID of the client.
     */
    private static SecureRandom prng;

    public TransfileRequestResponder(String rootPath)
    {
        fileSystem = new FileNode(rootPath, null, null);
        downloadsMap = new HashMap<Integer, SelectionKey>();
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method treat the requestpacket, and catch every Exception possible,
     * and then returns a Information or Error packets.
     * 
     * The second parameter Object object, can be used to give any object needed
     * for the current Request. For example in the case of a RqId, object will
     * be a SelectionKey, and it will store the SocketChannel command Key
     * associated to their ID, in the HashMap of the FileServer.
     * 
     * @param request
     *            the current request
     * @param object
     *            an optional object
     * @return the TransfilePackets corrresponding to the request
     */
    public TransfilePackets getResponse(Requests request, Object object)
    {
        switch (request.getRequestCode()) {
        case CD:
            try {
                return processChangeDirectory(((RqCd) request).getFileName());
            } catch (NotExistentResourceException e) {
                return new NotExistantFileOrDirectory();
            } catch (TransfileException e) {
                return new NotExistantFileOrDirectory();
            }

        case LS:
            return processListingDirectory();

        case GET:
            try {
                RqGet getRequest = (RqGet) request;
                return processGet(getRequest.getFileName());
            } catch (NotExistentResourceException e) {
                return new NotExistantFileOrDirectory();
            } catch (TransfileException e) {
                return new NotExistantFileOrDirectory();
            }

        case KILL:
            RqKill kill = (RqKill) request;
            try {
                return processKill(kill.getFileId());
            } catch (NotExistentResourceException e) {
                return new NotExistantTask();
            } catch (TransfileException e) {
                return new NotExistantTask();
            }

        case ID:
            int id = prng.nextInt();
            FileServer.clientMap.put(id, (SelectionKey) object);
            return new InfoId(id);

        case EXIT:
            if (object instanceof SelectionKey) {
                processExit((SelectionKey) object);
            }
            break;
        }

        return null;
    }

    /**
     * Process to a directory change based on the given path parameter. It will
     * modify the reference of the fileSystem. It will throw an exception in
     * case of the changeDirectory is not allowed or possible.
     * 
     * @param newPath
     *            the path to change the directory
     * @return return an InfoCd if it success
     * @throws TransfileException
     */
    public InfoCd processChangeDirectory(String newPath)
            throws TransfileException
    {
        Node newNode = fileSystem.getChildWithName(newPath);
        if (newNode.isFile()) {
            throw new NotExistentResourceException("Directory not found");
        }
        fileSystem = newNode;
        return new InfoCd(pwd());
    }

    /**
     * Process to the listing directory.
     * 
     * @return return the InfoLs associated to the current directory in the
     *         fileSystem
     */
    public InfoLs processListingDirectory()
    {
        return fileSystem.getLS();
    }

    /**
     * Process the Get based on the name. It will add to the taskFileMap the ID
     * of the file and the associated File object.
     * 
     * @param name
     *            the name of the asked download
     * @return an InfoGet if it success
     * @throws TransfileException
     */
    public InfoGet processGet(String name) throws TransfileException
    {
        Node downloadFileNode = fileSystem.getChildWithName(name);
        if (!downloadFileNode.isFile()) {
            throw new NotExistentResourceException("Resource not found");
        }

        Statistics.getInstance().addFileStats(downloadFileNode.getFile(), 1);
        taskFileMap.put(downloadFileNode.getId(), downloadFileNode.getFile());

        return new InfoGet(downloadFileNode.getId(), pwd()
                + downloadFileNode.getFile().getName(),
                FileSplitter.getTotalFragmentOfFile(downloadFileNode.getFile()));
    }

    /**
     * Kill a download task by fetching the Key of the download SocketChannel in
     * the downloadsMap, then it will terminate the key with the static method
     * FileServer.terminateKey
     * 
     * @param id
     *            the id of the task to kill
     * @return an InfoKill if it success
     * @throws TransfileException
     */
    public InfoKill processKill(int id) throws TransfileException
    {
        if (downloadsMap.get(id) == null) {
            throw new NotExistentResourceException("Task not found");
        }

        SelectionKey dlKey = downloadsMap.get(id);
        downloadsMap.remove(id);
        FileServer.terminateKey(dlKey);

        return new InfoKill(id);
    }

    public void processExit(SelectionKey cmdKey)
    {
        FileServer.terminateKey(cmdKey);

        for (Entry<Integer, SelectionKey> keyEntry : downloadsMap.entrySet()) {
            FileServer.terminateKey(keyEntry.getValue());
        }
    }

    /**
     * Add the key of a download SocketChannel associated to the ID of the
     * download.
     * 
     * @param id
     *            the id of the download
     * @param key
     *            the key of the SocketChannel of the download
     */
    public void addDownloadKey(int id, SelectionKey key)
    {
        downloadsMap.put(id, key);
    }

    /**
     * Remove the key of a download SocketChannel with the given download ID
     * 
     * @param id
     *            the download ID
     */
    public void removeDownloadKey(int id)
    {
        downloadsMap.remove(id);
    }

    /**
     * Return the current path in the fileSystem
     * 
     * @return the current path
     */
    public String pwd()
    {
        String currentFile = fileSystem.getFile().getAbsolutePath();
        String rootFile = fileSystem.getRoot().getFile().getAbsolutePath();
        String pwd = currentFile.replace(rootFile, "");

        if (pwd.equals("")) {
            pwd = File.separator;
        }
        if (!pwd.equals(File.separator)) {
            pwd = pwd + File.separator;
        }

        return pwd;
    }

    /**
     * Retrieve a File object based on the Id.
     * 
     * @param id
     *            the id of the file
     * @return the file asked
     */
    public File fetchFileById(int id)
    {
        return taskFileMap.get(id);
    }

}