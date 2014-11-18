package fr.upemlv.transfile.filesystem;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import fr.upemlv.transfile.exceptions.NotExistentResourceException;
import fr.upemlv.transfile.exceptions.TransfileException;
import fr.upemlv.transfile.packets.informations.InfoLs;
import fr.upemlv.transfile.structures.FileDate;
import fr.upemlv.transfile.structures.FileStructure;
import fr.upemlv.transfile.structures.FileStructureList;

public class FileNode implements Node
{
    /**
     * The current directory
     */
    private final File currentFile;

    /**
     * The children
     */
    private File[] fileList;

    /**
     * Reference to the parent
     */
    private final Node parent;

    /**
     * Reference to the root
     */
    private final Node root;

    /**
     * HashMap that contains the children File Object classed by name
     */
    private HashMap<String, File> fileMap;

    public FileNode(String path, Node parent, Node root)
    {
        this.currentFile = new File(path);
        this.parent = (parent == null) ? this : parent;
        this.root = (root == null) ? this : root;

        this.initMapFiles();
    }

    /**
     * Initiate the map containing as key the name of the children files and
     * value the File object
     */
    private void initMapFiles()
    {
        if (isFile()) {
            return;
        }
        
        this.fileList = currentFile.listFiles();
        this.fileMap = new HashMap<String, File>();
        
        for (File file : fileList) {
            fileMap.put(file.getName(), file);
        }
    }

    @Override
    public File getFile()
    {
        return currentFile;
    }

    @Override
    public Node getChildWithName(String name) throws TransfileException
    {
        this.initMapFiles();
        
        if (isFile()) {
            throw new NotExistentResourceException(
                    "A file do not have children file/directory.");
        }

        if (name.equals("")) {
            return this;
        }

        if (name.startsWith("..")) {
            if (name.startsWith("../")) {
                return getParent().getChildWithName(
                        name.replaceFirst("../", ""));
            }
            return getParent().getChildWithName(name.replaceFirst("..", ""));
        }

        if (name.startsWith("/")) {
            return getRoot().getChildWithName(name.replaceFirst("/", ""));
        }

        if (name.contains("/")) {
            int index = name.indexOf("/");
            String newPath = name.substring(0, index);
            String restPath = name.substring(index + 1, name.length());

            if (!isExist(newPath) || fileMap.get(newPath).isFile()) {
                return this;
            }

            return new FileNode(fileMap.get(newPath).getAbsolutePath(), this,
                    root).getChildWithName(restPath);
        }

        if (!isExist(name)) {
            throw new NotExistentResourceException("Resource does not exist.");
        }

        return new FileNode(fileMap.get(name).getAbsolutePath(), this, root);
    }

    @Override
    public Node getParent()
    {
        return parent;
    }

    @Override
    public Node getRoot()
    {
        return root;
    }

    @Override
    public int getChildCount()
    {
        return fileList.length;
    }

    @Override
    public boolean isExist(String name)
    {
        File test = fileMap.get(name);
        return (test != null) ? true : false;
    }

    @Override
    public boolean isFile()
    {
        return currentFile.isFile();
    }

    @Override
    public InfoLs getLS()
    {
        this.initMapFiles();
        
        FileStructureList listFiles = new FileStructureList();

        for (File file : fileList) {

            DateFormat dateFormat = DateFormat.getInstance();
            dateFormat.format(new Long(file.lastModified()));
            Calendar calendar = dateFormat.getCalendar();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            int size = Integer.parseInt(String.valueOf(file.length()));

            FileDate fileDate = new FileDate(year, month, day);
            String type = (file.isDirectory()) ? "D" : "F";
            FileStructure fileStruct = new FileStructure(size, fileDate,
                    file.getName(), type);

            listFiles.addFile(fileStruct);
        }

        return new InfoLs(listFiles);
    }

    @Override
    public int getId()
    {
        return getIdOfFile(currentFile);
    }

    @Override
    public String toString()
    {
        return currentFile.getAbsolutePath() + "\n" + fileMap.toString();
    }

    /**
     * Return the unique ID of a File based on the absolute path and the last
     * modified date.
     * 
     * @param file
     * @return the unique id of a File
     */
    public static int getIdOfFile(File file)
    {
        String id = file.getAbsolutePath() + file.lastModified();

        return id.hashCode();
    }

}
