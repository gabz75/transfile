package fr.upemlv.transfile.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.upemlv.transfile.exceptions.IllegalRequestException;
import fr.upemlv.transfile.exceptions.NotExistentResourceException;
import fr.upemlv.transfile.packets.Datas;
import fr.upemlv.transfile.packets.Errors;
import fr.upemlv.transfile.packets.Informations;
import fr.upemlv.transfile.packets.TransfileDecoder;
import fr.upemlv.transfile.packets.TransfilePackets;
import fr.upemlv.transfile.packets.data.AbstractData;
import fr.upemlv.transfile.packets.informations.InfoCd;
import fr.upemlv.transfile.packets.informations.InfoGet;
import fr.upemlv.transfile.packets.informations.InfoId;
import fr.upemlv.transfile.packets.informations.InfoKill;
import fr.upemlv.transfile.packets.informations.InfoTcpRequired;
import fr.upemlv.transfile.packets.informations.InfoUdpRequired;
import fr.upemlv.transfile.packets.requests.RqDownload;
import fr.upemlv.transfile.packets.requests.RqGet;
import fr.upemlv.transfile.settings.Settings;

/**
 * 
 * Represents the Client's Receive Thread
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 * 
 */
public class ClientReceive extends Thread {

	/**
	 * The main SocketChannel between the client and the server.
	 */
	private final SocketChannel channel;

	/**
	 * An instance of a decoder which grant the ability to return a new instance
	 * of any packet received.
	 */
	private final TransfileDecoder decoder = new TransfileDecoder(
			false, "-client");
	/**
	 * Our reader buffer, its size is set in class' Settings as a static field.
	 */
	private ByteBuffer reader = ByteBuffer.allocate(Settings.BUFF_SIZE);

	/**
	 * Our Threads pool in charge of all the current downloads.
	 */
	private final ExecutorService executors = Executors
			.newFixedThreadPool(Settings.MAX_THREAD);

	/**
	 * An HashMap representing all the current downloads.
	 * 
	 * key : File Id value : an instance of a FileDownload
	 */
	private final HashMap<Integer, FileDownload> currentDownloads;

	/**
	 * A list of the current FileDownloads
	 */
	private final List<FileDownload> listFiles = new LinkedList<FileDownload>();

	/**
	 * An HashMap representing all the unfinished downloads.
	 * 
	 * key : File id value : an instance of a FileDownload
	 */
	private final HashMap<Integer, FileDownload> unfinishedDownloads;

	/**
	 * An HashMap representing all the current MultiCastSocket currently used
	 * key : file ID value : an instance of a Multicast Socket
	 */
	private final HashMap<Integer, MulticastSocket> currentMulticastDownloads;

	/**
	 * The server Address
	 */
	private final SocketAddress server;

	/**
	 * An int which represents the id of the current user. This id is sent by
	 * the server and is unique.
	 */
	private int userID;

	/**
	 * The current Path upon the Server
	 */
	private String currentPath = File.separator;

	/**
	 * Constructor
	 * 
	 * @param channel
	 *            The main channel
	 */
	public ClientReceive(SocketChannel channel) {
		this.channel = channel;
		this.currentDownloads = new HashMap<Integer, FileDownload>();
		this.unfinishedDownloads = new HashMap<Integer, FileDownload>();
		this.currentMulticastDownloads = new HashMap<Integer, MulticastSocket>();
		server = new InetSocketAddress(Settings.serverAddress, Settings.port);
	}

	@Override
	public void run() {
		int x;
		try {
			while ((x = channel.read(reader)) != -1) {
				if (x > 0) {
					TransfilePackets packet = decoder.decode(reader);
					if (packet != null) {
						try {
							treatResponse(packet);
						} catch (NotExistentResourceException e) {
							System.err.println(e.getMessage());
							prompt();
						}
					}
				}
				reader.clear();
			}
		} catch (IOException e) {
			System.err.println("Channel for requests is down.");
		} catch (IllegalRequestException e) {
			System.err.println(e.getMessage());
		} finally {
			if (!currentDownloads.isEmpty())
				System.out.println("waiting for current downloads...");
			executors.shutdown();
			try {
				executors.awaitTermination(30, TimeUnit.MINUTES);
				System.out
						.println(" *********  Connection with TransFile Server is now closed ! **********\n Thank you for visiting.");
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tests the real type of the given packet, and calls the appropriate answer
	 * method.
	 * 
	 * @param packet
	 *            the packet received
	 * @throws NotExistentResourceException
	 * @throws IOException
	 */
	private void treatResponse(TransfilePackets packet)
			throws NotExistentResourceException, IOException {
		if (packet instanceof Datas) {
			treatData(packet);
		}
		if (packet instanceof Errors) {
			treatError(packet);
		}
		if (packet instanceof Informations) {
			treatInformation(packet);
		}
	}

	/**
	 * Treats the given packet as a data
	 * 
	 * @param packet
	 *            the packet received. Can be casted in AbstractData
	 * @throws NotExistentResourceException
	 * @throws IOException
	 */
	private void treatData(TransfilePackets packet)
			throws NotExistentResourceException, IOException {
		AbstractData datas = (AbstractData) packet;

		int fileID = datas.getId();
		if (!currentDownloads.containsKey(fileID)) {
			throw new NotExistentResourceException(
					"\nThis process download no longer exists");
		}
		FileDownload file = currentDownloads.get(fileID);
		file.write(datas.getDatas(), datas.getNumFragment());
	}

	/**
	 * Treats the given error packet
	 * 
	 * @param packet
	 *            the packet received. Can be casted in Error.
	 */
	private void treatError(TransfilePackets packet) {
		System.err.println(packet);
		prompt();
	}

	/**
	 * Tests the real type of the given Information Packet and treats it
	 * correctly. All the information types can be checked in InformationEnum.
	 * 
	 * @param packet
	 *            the packet received.
	 * @throws IOException
	 */
	private void treatInformation(final TransfilePackets packet)
			throws IOException {
		if (packet instanceof InfoGet) {
			InfoGet get = (InfoGet) packet;
			System.out.println("Download for the file : " + get.getFileName()
					+ " is now in progress...");
			prompt();
			try {
				final TransfileDecoder decoderDownload = new TransfileDecoder(
						false, "client-data");
				final ByteBuffer readerDownload = ByteBuffer
						.allocate(Settings.BUFF_SIZE);
				final SocketChannel downloadChannel = SocketChannel.open();
				downloadChannel.connect(server);
				executors.execute(new Runnable() {

					@Override
					public void run() {
						int x;
						try {
							sendUserId(downloadChannel);
							if (!sendDownloadRequest(packet, downloadChannel)) {
								terminateDownload(packet);
								return;
							}

							while ((x = downloadChannel.read(readerDownload)) != -1) {
								TransfilePackets packet = decoderDownload
										.decode(readerDownload);
								if (packet != null) {
									treatResponse(packet);
								}
								readerDownload.clear();
							}

							while (decoderDownload.hasRemaining()) {
								TransfilePackets remainingData = decoderDownload
										.decode(null);
								if (remainingData != null) {
									treatResponse(remainingData);
								}
							}

							terminateDownload(packet);

						} catch (IOException e) {
							System.err.println(e.getMessage());
						} catch (IllegalRequestException e) {
							System.err.println(e.getMessage());
						} catch (NotExistentResourceException e) {
							System.err.println(e.getMessage());
						} finally {
							prompt();
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} else if (packet instanceof InfoKill) {
			InfoKill kill = (InfoKill) packet;
			int id = kill.getId();
			unfinishedDownloads.put(id, currentDownloads.get(id));
			currentDownloads.remove(id);
		} else if (packet instanceof InfoUdpRequired) {
			final InfoUdpRequired udpRequired = (InfoUdpRequired) packet;
			final MulticastSocket multicastSocket = new MulticastSocket(channel
					.socket().getPort());
			multicastSocket.joinGroup(InetAddress.getByName(udpRequired
					.getAddress().toString()));
			currentMulticastDownloads.put(udpRequired.getId(), multicastSocket);
			final TransfileDecoder decoderMulti = new TransfileDecoder(
					false, "client-data");
			final byte[] b = new byte[Settings.BUFF_SIZE];
			executors.execute(new Runnable() {

				@Override
				public void run() {
					try {
						ByteBuffer readerMulti = ByteBuffer.wrap(b);
						DatagramPacket myPacket = new DatagramPacket(b,
								b.length);
						int nb = 0;
						int total = 0;
						FileDownload file = currentDownloads.get(udpRequired
								.getId());
						while (nb <= total && !multicastSocket.isClosed()
								&& !file.getPacketsMissing().isEmpty()) {
							multicastSocket.receive(myPacket);
							TransfilePackets packet = decoderMulti
									.decode(readerMulti);
							if (packet != null) {
								treatResponse(packet);
								if (!(packet instanceof AbstractData))
									return;
								AbstractData datas = (AbstractData) packet;
								nb = datas.getNumFragment();
								total = datas.getTotalFragment();
							}
							readerMulti.clear();
						}
						while (decoderMulti.hasRemaining()) {
							TransfilePackets remainingData = decoderMulti
									.decode(null);
							if (remainingData != null) {
								treatResponse(remainingData);
							}
						}

						ByteBuffer buffer = ByteBuffer.wrap(new RqGet(
								currentDownloads.get(udpRequired.getId())
										.getName()).buildDatas());
						while (buffer.hasRemaining()) {
							channel.write(buffer);
						}

						multicastSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalRequestException e) {
						e.printStackTrace();
					} catch (NotExistentResourceException e) {
						e.printStackTrace();
					} finally {
						prompt();
					}
				}

			});
			prompt();
			return;
		} else if (packet instanceof InfoTcpRequired) {
			InfoTcpRequired tcpRequired = (InfoTcpRequired) packet;
			int id = tcpRequired.getId();
			if (currentMulticastDownloads.containsKey(id)) {
				currentMulticastDownloads.get(id).close();
				currentMulticastDownloads.remove(id);
			}
			ByteBuffer buffer = ByteBuffer.wrap(new RqGet(currentDownloads.get(
					id).getName()).buildDatas());
			while (buffer.hasRemaining()) {
				channel.write(buffer);
			}
		} else if (packet instanceof InfoId) {
			InfoId idPacket = (InfoId) packet;
			this.userID = idPacket.getId();
			System.out
					.println("You are now correctly logged on the TransFile Server ! Your client ID is :"
							+ userID);
			prompt();
			return;
		} else if (packet instanceof InfoCd) {
			InfoCd cd = (InfoCd) packet;
			currentPath = cd.getMessage();
			prompt();
			return;
		}
		System.out.println(packet);
		prompt();
	}

	/**
	 * Closes properly the current download
	 * 
	 * @param packet
	 *            the infoget packet
	 * @throws IOException
	 */
	private void terminateDownload(TransfilePackets packet) throws IOException {
		InfoGet infoget = (InfoGet) packet;
		System.out.println("\nDownload of the -- "
				+ currentDownloads.get(infoget.getId()).getName()
				+ " -- is now ended.");
		currentDownloads.get(infoget.getId()).closeFileChannel();
		currentDownloads.remove(infoget.getId());
	}

	/**
	 * Sends a packet to the server containig our user ID
	 * 
	 * @param channel
	 *            the main channel
	 * @throws IOException
	 */
	private void sendUserId(SocketChannel channel) throws IOException {
		ByteBuffer writer = ByteBuffer.wrap(new InfoId(this.userID)
				.buildDatas());
		while (writer.hasRemaining()) {
			channel.write(writer);
		}
	}

	/**
	 * Tests whether the file ID concerned is already currently downloading, or
	 * has been interrupted by a kill request, or has never begun so a new
	 * FileDownload will be created.
	 * 
	 * In all cases, the correct number of RequestDownload will be sent thanks
	 * to sendOptimumRequest.
	 * 
	 * @param packet
	 *            the packet
	 * @param channel
	 *            the download channel
	 * @return true whether some RequestDownload has been sent, false if the
	 *         file is already completed.
	 * @throws IOException
	 */
	private boolean sendDownloadRequest(TransfilePackets packet,
			SocketChannel channel) throws IOException {
		InfoGet get = (InfoGet) packet;
		if (!currentDownloads.containsKey(get.getId())) {
			if (unfinishedDownloads.containsKey(get.getId())) {
				int id = get.getId();
				currentDownloads.put(id, unfinishedDownloads.get(id));
				unfinishedDownloads.remove(id);
			} else {
				FileDownload fd = null;

				for (FileDownload s : listFiles) {
					if (s.getName().equals(get.getFileName())) {
						s.closeDownloadChannel();
						fd = s;
						break;
					}
				}
				if (fd != null) {
					currentDownloads.remove(fd);
					listFiles.remove(fd);
					fd.closeFileChannel();
				}
				FileDownload dl = new FileDownload(get.getId(),
						get.getFileName(), channel);
				dl.setSize(get.getTotalFragment());
				currentDownloads.put(get.getId(), dl);
				listFiles.add(dl);
			}

		} else
			return false;
		return sendOptimumRequest(get, channel);
	}

	/**
	 * Sends as many RequestDownload as necessary by asking all the missing
	 * fragments.
	 * 
	 * @param get
	 *            the packet
	 * @param downloadChannel
	 *            the download channel
	 * @return true whether some RequestDownload has been sent, false if the
	 *         file is already completed.
	 * @throws IOException
	 */
	private boolean sendOptimumRequest(InfoGet get,
			SocketChannel downloadChannel) throws IOException {
		boolean packetSent = false;
		int start = Settings.FILE_START;
		int end = Settings.FILE_START;
		int totalMissing = currentDownloads.get(get.getId())
				.getPacketsMissing().size();
		int nb = 0;
		int isLast = Settings.MORE_FRAGMENT;
		boolean[] packets = currentDownloads.get(get.getId()).getPackets();
		for (int i = 0; i < packets.length; i++) {
			if (packets[i]) {
				if (!packets[start - 1]) {
					nb += i - start + 1;
					if (nb == totalMissing)
						isLast = Settings.LAST_FRAGMENT;
					send(get, downloadChannel, start, i, isLast);
					packetSent = true;
					end++;
					start = end;
				} else {
					start++;
					end++;
				}
			} else {
				end++;
			}
		}
		if (!packets[packets.length - 1]) {
			packetSent = true;
			send(get, downloadChannel, start, Settings.FILE_END,
					Settings.LAST_FRAGMENT);
		}
		return packetSent;
	}

	/**
	 * Sends a RequestDownload to the server on the correct Download Channel
	 * 
	 * @param get
	 *            the packet
	 * @param downloadChannel
	 *            the download channel
	 * @param start
	 *            the number of fragment to start
	 * @param end
	 *            the number of fragment to stop.
	 * @param isLast
	 *            indicates whether this request is the last or not.
	 * @throws IOException
	 */
	private void send(InfoGet get, SocketChannel downloadChannel, int start,
			int end, int isLast) throws IOException {
		ByteBuffer writer = ByteBuffer.wrap(new RqDownload(get.getId(), start,
				end, isLast).buildDatas());
		while (writer.hasRemaining()) {
			downloadChannel.write(writer);
		}
	}

	/**
	 * Generates a status of all the current downloads.
	 * 
	 * @return a status of all current downloads.
	 */
	public String statusToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Files in progress.... \n");
		if (currentDownloads.values().isEmpty())
			sb.append("none\n");
		for (FileDownload file : currentDownloads.values()) {
			sb.append(file.toString());
		}
		sb.append("end of status request");
		return sb.toString();
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void prompt() {
		if (channel.isConnected())
			System.out.print(currentPath + " > ");
	}

}
