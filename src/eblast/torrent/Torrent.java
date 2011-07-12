/*
 * This file is part of eBlast Project.
 *
 * Copyright (c) 2011 eBlast
 *
 * eBlast is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eBlast is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eBlast.  If not, see <http://www.gnu.org/licenses/>.
 */

package eblast.torrent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eblast.bencoding.InvalidBEncodingException;
import eblast.io.SpeedPair;
import eblast.io.TorrentFile;
import eblast.io.FileManager;
import eblast.checksum.Hash;
import eblast.checksum.NullHashException;
import eblast.log.Log;
import eblast.metainfo.AnnounceList;
import eblast.metainfo.Info;
import eblast.metainfo.MetaInfo;
import eblast.torrent.peer.Peer;
import eblast.torrent.peer.PeerHandler;
import eblast.torrent.peer.PeerIDGenerator;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.PieceLengthException;
import eblast.torrent.piece.PieceManager;
import eblast.torrent.piece.WrongIndexException;
import eblast.torrent.tracker.AnnounceInfo;
import eblast.torrent.tracker.TrackerInfo;
import eblast.torrent.tracker.TrackerInfoException;

/**
 * This class represents a torrent.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 04.03.2011 - Initial version
 * @version 1.1 - 15.03.2011 - Implementation of peer creation list
 * @version 1.2 - 18.05.2011 - Implementation of speed counter
 */
public class Torrent {
	
	public static enum TorrentStates { started, stopped, completed, checking }; // Different torrent states
	public static final int ONE_MINUTE = 60000; // 1 minute
	
	private String mName;
	private String mAnnounce;				// Default tracker (address only)
	private AnnounceList mAnnounceList;		// List of backup trackers (address only)
	private Hash mInfoHash;
	private String mPeerID;
	
	private List<TrackerInfo> mTrackers;	// List of all the trackers (with all the informations)
	private List<Peer> mPeers;				// List of all the peers
	private List<PeerHandler> mPeerHandlers;// List of all the Peerhandlers
	private List<Peer> mConnectedPeers;
	private List<Piece> mPieces;			// List of all the pieces
	private List<Hash> mPieceHashes;
	private Set<Integer> mReceivedPieces;	// Pieces that we already have
	private List<TorrentFile> mFiles;		// Files contained into the torrent (from metainfo)
	private Set<Integer> mAvailablePieces;	// Pieces that we long to obtain
	
	private int mNumWant;					// Max. number of pieces we would like to get.
	private long mPieceLength;				// Length of a piece by default
	private boolean mCompact;				// The tracker sends us the peers list in a compact format if true.
	private long mLeft;						// Number of bytes left to download
	private long mUploaded;					// Total number of uploaded bytes
	private long mDownloaded;				// Total number of downloaded bytes
	private FileManager mFileManager;		// Needed to write blocks on the FileSystem
	private TorrentStates mEvent;			// Stores the current event of this torrent
	
	private PieceManager mPieceManager;		// Manages all the pieces and the request of new pieces to the peers
	
	// Used in GUI
	private long 	mLength;
	private Date	mCreationDate;
	private String	mCreatedBy;
	private String	mEncoding;
	private String	mComment;
	
	/**
	 * Create an instance of Torrent from a MetaInfo and a Port
	 * @param metainfo Parsed MetaInfo that contains all informations about the Torrent.
	 * @param port Number of port where the others peers are going to be connected to.
	 * @return An Instance of Torrent created with this MetaInfo
	 * @throws IOException 
	 * @throws TorrentException 
	 * @throws WrongIndexException 
	 * @throws BlockLengthException 
	 * @throws NullHashException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static Torrent createTorrent(MetaInfo metainfo) throws IOException, TorrentException, NullHashException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		return new Torrent(metainfo, PeerIDGenerator.generateID());
	}
	
	/**
	 * Constructor of Torrent, it initializes the Torrent.
	 * @param metainfo MetaInfo that contains all informations about the Torrent.
	 * @param peerID ID that is going to be send to trackers.
	 * @throws IOException 
	 * @throws TorrentException 
	 * @throws WrongIndexException 
	 * @throws BlockLengthException 
	 * @throws NullHashException 
	 * @throws NoSuchAlgorithmException 
	 */
	private Torrent(MetaInfo metainfo, String peerID) throws IOException, TorrentException, NullHashException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		Log.i("Torrent", "Create Torrent (" + peerID + ") on port " + getPort());
		
		mPeerID = peerID;
		mCompact = true;
		mNumWant = 60;
		mEvent = TorrentStates.stopped;
		mPeers = Collections.synchronizedList(new ArrayList<Peer>());
		mConnectedPeers = Collections.synchronizedList(new ArrayList<Peer>());
		mPeerHandlers = Collections.synchronizedList(new ArrayList<PeerHandler>());

		// Instanciate a metaInfo reader.
		mInfoHash = metainfo.getInfoHash();
		Info info = metainfo.getInfo();

		mLength = info.getLength();
		
		mCreationDate = metainfo.getCreationDate();
		mCreatedBy = metainfo.getCreatedBy();
		mEncoding = metainfo.getEncoding();
		mComment = metainfo.getComment();
		
		mName = info.getName();
		mPieceLength = info.getPieceLength();
		mPieceHashes = info.getPiecesHashes();
		
		if (info.getFiles().size() > 1) {
			throw new TorrentException("The multi-file isn't implemented yet.");
		}
		
		mFiles = info.getFiles();
		mLeft = mLength;
		mUploaded = 0;
		mDownloaded = 0;
		
		// Get the Tracker informations...
		mAnnounce = metainfo.getAnnounce();
		mAnnounceList = metainfo.getAnnounceList();
		initTrackersList();
		
		// Instanciate the FileManager.
		mFileManager = new FileManager(this);
		initPieces();

		// Instanciate a PieceManager.
		mPieceManager = new PieceManager(this);
		
		mReceivedPieces = new HashSet<Integer>();
		mAvailablePieces = new HashSet<Integer>();
	}
	
	/**
	 * Add a tracker to the trackers list.
	 * @param tracker address of the tracker to be added
	 */
	private void addTracker(String tracker) {
		try {
			TrackerInfo ti = new TrackerInfo(this, tracker);
			if (!mTrackers.contains(ti)) {
				mTrackers.add(ti);
			}
		} catch (TrackerInfoException e) {
			// Already logged, and the tracker will just be ignored.
		}
	}
	
	/**
	 * Initializes the trackers List from the MetaInfo Announce & AnnounceList
	 */
	private void initTrackersList() {
		mTrackers = new ArrayList<TrackerInfo>();
		
		// Add the default Tracker.
		addTracker(mAnnounce);
		
		if (mAnnounceList != null) {
			for (int i=0, size = mAnnounceList.size(); i<size; i++) { // All lists of trackers
				try {
					for (String tracker: mAnnounceList.get(i)) { // All trackers in the list
						addTracker(tracker);
					}
				} catch (InvalidBEncodingException e) {
					Log.e("AnnounceList", "Unable to read the announce list.");
				}
			}
		}
	}
	
	/**
	 * Add a peer into the PeerList, however add only the ones that aren't already in the list.
	 * @param peer The peer to be added.
	 */
	public void addPeer(Peer peer) {
		
		if (mEvent.equals(TorrentStates.stopped)) return;
		
		synchronized (mPeers) {
			if (!mPeers.contains(peer)) {
				mPeers.add(peer);
			}
		}
	}
	
	/**
	 * Allow to shuffle the whole peers list.
	 */
	public void shufflePeers() {
		synchronized (mPeers) {
			Collections.shuffle(mPeers);
		}
	}
	
	/**
	 * Establish a connection between us and a peer.
	 * @param peer Peer to which we want to connect.
	 */
	public void connectToPeer(Peer peer) {
		synchronized (mPeerHandlers) {
			mPeerHandlers.add(new PeerHandler(this, peer));
			mPeers.remove(peer);
			mConnectedPeers.add(peer);
		}
	}
	
	/**
	 * Add a peer that the socket has already been open.
	 * @param peer The peer to be added.
	 * @param socket Socket connected with the Peer.
	 * @param encrypted if the peer accept encrypted connexion
	 */
	public void addPeer(Peer peer, Socket socket, boolean encrypted) {
		synchronized (mPeers) {
			if (mEvent.equals(TorrentStates.stopped)) return;
			
			if (!mPeers.contains(peer)) {
				mPeers.add(peer);
			}
		}
	}
	
	/**
	 * Remove a PeerHandler from the list.
	 * @param peerHandler the peerHandler that manage the peer. 
	 * @param error Is there after an error ?
	 */
	public void removePeerHandler(PeerHandler peerHandler, boolean error) {
		Peer peer = peerHandler.getPeer();
		synchronized (mPeerHandlers) {
			mPeerHandlers.remove(peerHandler);
		}
		synchronized (mConnectedPeers) {
			mConnectedPeers.remove(peer);
		}
		if (!error) { // Because an error has occured, we removed the peer from our list as well.
			synchronized (mPeers) {
				mPeers.add(peer);
			}
		}

		Log.i("Torrent", "Disconnected from " + peer);
	}

	/**
	 * Executes a peersCall on the Tracker.
	 * @param tracker tracker that is going to receive the call
	 */
	private void peersCall(final TrackerInfo tracker) {
		
		// We create the call into a Thread to inhibit the wait-the-answer block
		// It's a one-time execution
		new Thread(tracker.toString()) {
			public void run() {
				try {
					AnnounceInfo announce = tracker.getAnnounceInfo(createTrackerMap());
					List<Peer> peerList = announce.getPeersList();
					
					if (peerList != null) {
						
						// Add all peers to the list.
						for (Peer p: peerList) {
							addPeer(p);
						}
						
						// And finally shuffle it.
						shufflePeers();
					}
				} catch (TrackerInfoException e) {
					// This exception is already going to send a message into the Log System.
					// And the error is stored in the TrackerInfo.
				}
			}
		}.start();
	}
	
	/**
	 * Do a massive peersCall on each tracker. 
	 */
	public void massPeersCall() {
		for (TrackerInfo tracker: mTrackers) {
			peersCall(tracker);
		}
	}
	
	/**
	 * Creates a Map that contains all the key/values to send to the tracker.
	 * @return Dictionary of Keys/Values to send to the Tracker.
	 */
	private Map<String, String> createTrackerMap() {
		Map<String, String> map = new HashMap<String, String>();
		Log.d("InfoHash", mInfoHash.toURLString());
		map.put(TrackerInfo.KEY_INFO_HASH,	mInfoHash.toURLString());
		map.put(TrackerInfo.KEY_PEER_ID,	mPeerID);
		map.put(TrackerInfo.KEY_PORT,		String.valueOf(getPort()));
		map.put(TrackerInfo.KEY_LEFT,		String.valueOf(mLeft));
		map.put(TrackerInfo.KEY_COMPACT,	mCompact ? "1" : "0");
		
		switch (mEvent) {
		case started:
			map.put(TrackerInfo.KEY_EVENT,	TrackerInfo.KEY_EVENT_STARTED);
			break;
			
		case completed:
			map.put(TrackerInfo.KEY_EVENT,	TrackerInfo.KEY_EVENT_COMPLETED);
			break;
			
		case stopped:
			map.put(TrackerInfo.KEY_EVENT,	TrackerInfo.KEY_EVENT_STOPPED);
			break;
		}
		
		map.put(TrackerInfo.KEY_NUMWANT,	String.valueOf(mNumWant - mPeerHandlers.size()));
		
		map.put(TrackerInfo.KEY_DOWNLOADED,	String.valueOf(mDownloaded));
		map.put(TrackerInfo.KEY_UPLOADED,	String.valueOf(mUploaded));
		map.put(TrackerInfo.KEY_KEY,		"null");
		
		return map;
	}
	
	/**
	 * Creates all the Piece structure.
	 * @throws NullHashException
	 * @throws BlockLengthException
	 * @throws WrongIndexException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private void initPieces() throws NullHashException, BlockLengthException, WrongIndexException, IOException, NoSuchAlgorithmException {
		
		// Creation of the Piece Array contained into the torrent.
		int arraySize = (int) ((mLength + mPieceLength - 1) / mPieceLength);
		mPieces = new ArrayList<Piece>(arraySize);
		long len = mLength;
		for (int i=0; i < arraySize; i++) {
			
			try {
				// If it's the last piece and if it has a different size from the others.
				mPieces.add(new Piece(mFileManager, i, (int) Math.min(len, mPieceLength), mPieceHashes.get(i)));
				
				len -= mPieceLength;
			} catch (PieceLengthException e) {}
		}
	}
	
	/**
	 * Returns the current download directory.
	 * @return current download directory.
	 */
	public String getDownloadDir() {
		return TorrentManager.getInstance().getSettings().getDownloadDir().getAbsolutePath();
	}
	
	/**
	 * Check whether the torrent is complete.
	 * @return true if the torrent has been completely downloaded, returns false otherwise.
	 */
	public boolean isComplete() {
		
		for (Piece p : mPieces) {
			if (!p.isComplete()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the completeness of the torrent.
	 * @return completeness of the torrent
	 */
	public double getCompleteness() {
		double completeness = 0;
		
		for (Piece p : mPieces) {
			completeness += (double)p.getDownloadCompleteness() / (double)mPieces.size();
		}
		
		return completeness;
	}
	
	/**
	 * Stop the torrent. It will disconnect all Peers and PeerHandlers.
	 */
	public void stopTorrent() {
		mEvent = TorrentStates.stopped;
	}
	
	/**
	 * Closes all files (and remove them if asked).
	 * @param eraseAll Do we need to remove files from the disk?
	 */
	public void destroyTorrent(boolean eraseAll) {
		if (eraseAll) {
			mFileManager.erase();
		} else {
			mFileManager.close();			
		}
	}
	
	/**
	 * Launch a thread that will start the Torrent and do some stats.
	 */
	public void startTorrent() {
		if (mEvent.equals(TorrentStates.stopped)) {
			new Thread("Torrent(" + mName + ")") {
				public void run() {
					mEvent = TorrentStates.checking;
					for (Piece piece: mPieces) {
						try {
							piece.init();
						} catch (NullHashException e) {}
						catch (NoSuchAlgorithmException e) {
							Log.e("Torrent::NoSuchAlgorithmException", "NoSuchAlgorithmException");
						} catch (UnsupportedEncodingException e) {
							Log.e("UnsupportedEncodingException", "UnsupportedEncodingException");
						}
					}
					
					mPieceManager.launch();
					
					mEvent = isComplete() ? TorrentStates.completed : TorrentStates.started;
					massPeersCall();
					
					while (!mEvent.equals(TorrentStates.stopped)) {
						
						if (isComplete() && !mEvent.equals(TorrentStates.completed)) {
							Log.i("PieceManager", "Torrent is complete");
							setTorrentState(TorrentStates.checking);
							boolean checked = true;
							synchronized (mPieces) {
								for (Piece p: getPieces()) {
									try {
										checked &= p.init();						
									} catch (Exception e) {}
								}
								setTorrentState(checked ? TorrentStates.completed : TorrentStates.started);								
							}
						}
						
						// Connect to peers
						while ((mPeerHandlers.size() < mNumWant) && (mPeers.size() > 0)) {
							connectToPeer(mPeers.get(0));
						}
						
						mReceivedPieces.clear();
						mLeft = mLength;
						for (Piece p: mPieces) {
							mLeft -= p.getLeft();
							if (p.isComplete()) {
								mReceivedPieces.add(p.getIndex());
							}
						}
						
						synchronized (mPeerHandlers) {
							for (PeerHandler ph: mPeerHandlers) {
								mAvailablePieces.addAll(ph.getPeerAvailablePieces());
							}							
						}
						
						synchronized (mTrackers) {
							for (TrackerInfo t: mTrackers) { // Update the tracker every minute.
								if (t.needToBeUpdated(ONE_MINUTE)) {
									peersCall(t);
								}
							}
						}
						
						try {
							Thread.sleep(2000L);
						} catch (InterruptedException e) {}
					}
					
					synchronized (mPeerHandlers) {
						// Disconnect all Peer from the tracker, and remove them
						for (int i=0; i<mPeerHandlers.size(); i++) {
							mPeerHandlers.get(i).disconnect();
						}
						mPeerHandlers.clear();
					}
					mPeers.clear(); // Remove all peers
				}
			}.start();
		}
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Return a copy of the list of Pieces.
	 * @return Copy of the list of Pieces.
	 */
	public List<Piece> getPieces() {
		List<Piece> backup = new ArrayList<Piece>(mPieces.size());
		for (Piece currentPiece : mPieces)
		   backup.add(currentPiece);
		
		return backup;
	}

	/**
	 * Returns the instance of the PieceManager of the current Torrent.
	 * @return instance of the PieceManager of the current Torrent.
	 */
	public PieceManager getPieceManager() {
		
		return mPieceManager;
	}
	
	/**
	 * Returns the length of the current Piece.
	 * @return length of the current Piece.
	 */
	public long getPieceLength() {
		
		return mPieceLength;
	}
	
	/**
	 * @return The number of pieces into the Torrent.
	 */
	public int getPieceCount() {
		return mPieceHashes.size();
	}
	
	/**
	 * @return A set containing all pieces that have been received. 
	 */
	public Set<Integer> getReceivedPieces() {
		return mReceivedPieces;
	}
	
	/**
	 * @return A set containing all pieces that could be able to download from peers.
	 */
	public Set<Integer> getAvailablePieces() {
		return mAvailablePieces;
	}

	/**
	 * @return A list of all tracker that we are connected.
	 */
	public List<TrackerInfo> getTrackers() {
		return mTrackers;
	}

	/**
	 * Return the current File (where we are going to write the data to).
	 * @return the current File.
	 */
	public List<TorrentFile> getFiles() {
		return mFiles;
	}
	
	/**
	 * @return The FileManager for this Torrent.
	 */
	public FileManager getFileManager() {
		return mFileManager;
	}
	
	/**
	 * @return Port to connect to this Torrent.
	 */
	public int getPort() {
		return TorrentManager.getInstance().getSettings().getPort();
	}

	/**
	 * @return InfoHash to authentificate this Torrent.
	 */
	public Hash getInfoHash() {
		return mInfoHash;
	}
	
	/**
	 * @return The PeerID we send to other peers.
	 */
	public String getPeerID() {
		return mPeerID;
	}
	
	/**
	 * Change the actual state of the Torrent.
	 * @param event State in which the Torrent will be.
	 */
	public void setTorrentState(TorrentStates event) {
		mEvent = event;
	}
	
	/**
	 * @return The actual state of the Torrent (started, stopped, completed, checking).
	 */
	public TorrentStates getTorrentState() {
		return mEvent;
	}
	
	/**
	 * @return A pair that contains both download and upload speed in bytes per second.
	 */
	public SpeedPair getSpeed() {
		double download = 0;
		double upload = 0;
		
		synchronized (mPeerHandlers) {
			for (PeerHandler ph : mPeerHandlers) {
				SpeedPair speed = ph.getSpeed();
				download += speed.download;
				upload += speed.upload;
			}			
		}
		
		return new SpeedPair(download, upload);
	}

	/**
	 * Simple method to get the name of the Torrent:
	 * <ul>
	 * 		<li> Filename: in single file mode
	 * 		<li> Directory name: in multi-files mode
	 * </ul>
	 */
	public String toString() {
		return mName;
	}
	
	/**
	 * Compare if two object are the same Torrent.
	 * @return o Torrent Object to compare.
	 */
	public boolean equals(Object o) {
		try {
			Torrent t = (Torrent)o;
			return mInfoHash.equals(t.mInfoHash);
			
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	/**
	 * @return When the MetaInfo has been created.
	 */
	public Date getCreationDate() {
		return mCreationDate;
	}

	/**
	 * @return Who has created the MetaInfo.
	 */
	public String getCreatedBy() {
		return mCreatedBy;
	}

	/**
	 * @return Encoding used to generate the Piece Part of the MetaInfo.
	 */
	public String getEncoding() {
		return mEncoding;
	}
	
	/**
	 * @return Comment added to describe the MetaInfo.
	 */
	public String getComment() {
		return mComment;
	}
	
	/**
	 * @return length of the torrent in byte.
	 */
	public long getLength() {
		return mLength;
	}
	
	/**
	 * @return how many byte we still have to downloaded.
	 */
	public long getLeft() {
		return mLeft;
	}

	/**
	 * @return list of all peers.
	 */
	public List<Peer> getPeers() {
		return mPeers;
	}
	
	/**
	 * @return list of all peers.
	 */
	public List<Peer> getConnectedPeers() {
		return mConnectedPeers;
	}
}
