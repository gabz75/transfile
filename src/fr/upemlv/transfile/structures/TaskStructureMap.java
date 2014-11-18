package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;
import java.util.HashMap;

import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.packets.TransfilePackets;

/**
 * Represents a Map if TaskStructures
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 * 
 */
public class TaskStructureMap implements TransfilePackets {

	/**
	 * The HashMap of TaskStructures key : file Id value : The TaskStructure
	 */
	private final HashMap<Integer, TaskStructure> taskMap;

	/**
	 * The total length in bytes of the Map
	 */
	private int length;

	/**
	 * Constructor
	 */
	public TaskStructureMap() {
		taskMap = new HashMap<Integer, TaskStructure>();
		length = Integer.SIZE / 8;
	}

	/**
	 * Adds the TaskStructure to the map. Updates the length
	 * 
	 * @param t
	 *            the TaskStructure
	 */
	public void addTask(TaskStructure t) {
		taskMap.put(t.getId(), t);
		length += t.getLength();
	}

	/**
	 * Removes the TaskStructure to the map. Updates the length
	 * 
	 * @param t
	 *            the TaskStructure
	 */
	public void removeTask(TaskStructure t) {
		taskMap.remove(t.getId());
		length -= t.getLength();
	}

	/**
	 * Gets the TaskStructure corresponding to the file id given in parameter
	 * 
	 * @param id
	 *            the file id
	 * @return the TaskStructure
	 */
	public TaskStructure getTask(int id) {
		return taskMap.get(id);
	}

	/**
	 * Tests whether the TaskStructure is in the map
	 * 
	 * @param t
	 *            the TaskStructure
	 * @return true if the map contains t, false otherwise
	 */
	public boolean contains(TaskStructure t) {
		return taskMap.containsKey(t.getId());
	}

	/**
	 * Gets the length
	 * 
	 * @return length
	 */
	public int getLength() {
		return length;
	}

	@Override
	public byte[] buildDatas() {
		ByteBuffer bb = ByteBuffer.allocate(length);

		bb.putInt(taskMap.size());
		for (TaskStructure taskStructures : taskMap.values()) {
			bb.put(taskStructures.buildDatas());
		}

		return bb.array();
	}

	/**
	 * Decodes a TaskStructureMap from the given ByteBuffer given in parameter
	 * 
	 * @param bbr the ByteBuffer
	 * @return a new Instance of a TaskStructureMap
	 * @throws UncompletedPackageException
	 */
	public static TaskStructureMap decode(ByteBuffer bbr)
			throws UncompletedPackageException {
		TaskStructureMap list = new TaskStructureMap();
		int nbTasks = bbr.getInt();
		for (int i = 0; i < nbTasks; i++) {
			list.addTask(TaskStructure.decode(bbr));
		}
		return list;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (taskMap.size() == 0) {
			sb.append("O downloads in progress");
		}
		for (TaskStructure t : taskMap.values()) {
			sb.append(t.toString() + "\n");
		}

		return sb.toString();
	}

}
