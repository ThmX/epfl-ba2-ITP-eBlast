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

package eblast.torrent.peer;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eblast.crypto.KeyGenerator;
import eblast.crypto.RSAKeyPair;
import eblast.crypto.XORKeyPair;
import eblast.io.CounterInputStream;
import eblast.io.CounterOutputStream;
import eblast.io.CryptoInputStream;
import eblast.io.CryptoOutputStream;
import eblast.io.SpeedPair;
import eblast.log.Log;
import eblast.settings.EBlastSettings;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentManager;
import eblast.torrent.messages.*;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.PieceManager;

/**
 * This class manage the action with and by a Peer. 
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 12.04.2011 - Initial version
 * @version 1.1 - 21.05.2011 - Ajout de l'encryption
 */
public class PeerHandler extends Thread {
	
	private static final int MAX_REQUEST = 100;
	private static final long ACTIVE_DELAY = 100L;
	private static final long ONE_MINUTE_AND_A_HALF = 90000L;	// 1'30" in milliseconds
	
	private Torrent mTorrent;
	private Peer mPeer;
	private PieceManager mPieceManager;				// Will help us in knowing what pieces do we need.
	private MessageHandler mMessageHandler;
	
	private List<Request> mPendingRequests;			// Request messages that we have to send to the remote peer.
	private List<Message> mMessagesToSendQueue;		// All the other types of messages that we have to send to the remote peer.
	
	private boolean mAmIInterested;					// Am I Interested ?
	private boolean mAmIChoked;						// Am I choked ?
	private boolean mIsHeInterested;				// Is he interested ?
	private boolean mIsHeChoked;						// Is he choked ?
	
	private Socket mSocket;
	private boolean mAccepted;						// Has he been accepted by the PeerAccepter ?
	
	private CounterInputStream mCounterInput;
	private CounterOutputStream mCounterOutput;

	private MessageInputStream mInput;
	private MessageOutputStream mOutput;
	
	private EBlastSettings mSettings;
	
	// Cryptography
	private RSAKeyPair mRSAKeyPair;
	private XORKeyPair mMySymmetricKeyPair;
	
	
	private Set<Integer> mPeerPiecesAvailable;		// Pieces that the remote peer possess.
	
	private Date mNextKeepAlive;					// Last time the KeepAlive message has been sent.
	
	private boolean mPeerIsEncrypted;
	
	private boolean mActive;

	/**
	 * Default constructor.
	 * @param torrent Torrent related to the torrent
	 * @param peer remote peer
	 */
	public PeerHandler(Torrent torrent, Peer peer) {
		super("PeerHandler(" + peer + ")");
		
		Log.d("PeerHandler", "New PeerHandler with " + peer); // Debug
		
		mSocket = peer.getSocket();
		mAccepted = mSocket != null;
		mPeerIsEncrypted = peer.isEncrypted();
		
		mTorrent = torrent;
		mPeer = peer;
		
		mSettings = TorrentManager.getInstance().getSettings();
		
		mPieceManager = mTorrent.getPieceManager();
		
		mMessageHandler = new MessageHandler(this);

		mMessagesToSendQueue = Collections.synchronizedList(new LinkedList<Message>());
		mPendingRequests = Collections.synchronizedList(new LinkedList<Request>());
		
		mPeerPiecesAvailable = new HashSet<Integer>();
		
		mAmIInterested = false;
		mAmIChoked = true;
		mIsHeInterested = false;
		mIsHeChoked = true;
		
		mNextKeepAlive = new Date( new Date().getTime() + ONE_MINUTE_AND_A_HALF );
		
		mActive = true;
		
		this.start();
	}
	
	/**
	 * 1. Try to connect to the peer, otherwise quit
	 * 2. Try to handshake
	 * 3. Send our BitField
	 * 4. In a loop:
	 * 		a. Clean all request older than five seconds
	 * 		b. Get and manage at most 10 messages
	 * 		c. Send a KeepAlive message if necessary
	 * 		d. Create and send (at most) requests
	 * 		e. Send all messages in the queue
	 * 		f. Update the peer informations
	 */
	public void run() {
		
		try {
			// 1. We create a socket if it has not been created already.
			if (mSocket == null) {
				
				// Try to connect to the peer and get I/O Stream
				Log.d("PeerHandler", "Try connect to " + mPeer);
				mSocket = new Socket(mPeer.getIP(), mPeer.getPort()); // We create a new Socket.
			}
			
			mCounterInput = new CounterInputStream(mSocket.getInputStream());
			mCounterOutput = new CounterOutputStream(mSocket.getOutputStream());
			
			mInput = new MessageInputStream(mCounterInput);
			mOutput = new MessageOutputStream(mCounterOutput);

		} catch (UnknownHostException e) {
			Log.e("PeerHandler", "Unknown host (" + mPeer + ")");
			disconnectFromError();
			return;

		} catch (IOException e) {
			Log.e("PeerHandler", e.getMessage() + " with " + mPeer);
			disconnectFromError();
			return;
		}
		
		try {
			// 2. Send the HandShake to the peer.
			//System.out.println("Byte ??" + mInput.readByte());
			if (!handshake()) {
				Log.e("PeerHandler", "Handshake: Dropping the connection");
				disconnect();
				return;
			}
			
			// Try to activate the encryption if possible
			if (mSettings.isEncryptionActivated() && mPeerIsEncrypted) {
				activateEncryption();
			}
			
			// 3. Send the BitField through the OutputStream
			BitField bitField = new BitField(mTorrent.getPieces());
			mOutput.write(bitField);

			// 4. In a loop
			while (mActive && !mTorrent.getTorrentState().equals(Torrent.TorrentStates.stopped)) {
				
				// a. Clean old request
				cleanOldRequest();
				
				// b. Get and manage Message (with the MessageHandler by Visitor)
				for (int i=0; i<MAX_REQUEST && mInput.available() > 0; i++) { // At most 10 requests
					Message msg = mInput.readMessage();
					if (msg != null) {
						msg.accept(mMessageHandler);
					}
				}

				// c. Keep the torrent alive
				keepAlive();
				
				// d. Create and Send requests to the peer
				for (int i=0; i<MAX_REQUEST; i++) { // At most 10 requests
					createAndManageRequest();
				}
				
				// e. Send all message from the queue
				synchronized (mMessagesToSendQueue) {
					for (Message m: mMessagesToSendQueue) {
						mOutput.write(m);
					}
					mMessagesToSendQueue.clear(); // All message has been sent, we can clear them now.
				}
				
				// f. Update informations about the peer
				updatePeerInformation();
				
				try {
					sleep(ACTIVE_DELAY);
				} catch (InterruptedException e) {}
			}
			
		} catch (NullPointerException e) {
			disconnectFromError();

		} catch (MessageException e) {
			Log.e("PeerHandler", "Message error with " + mPeer);
			disconnectFromError();
			
		} catch (UnknownHostException e) {
			Log.e("PeerHandler", "Unknown host (" + mPeer + ")");
			disconnectFromError();

		} catch (IOException e) {
			Log.e("PeerHandler", "Connexion error with " + mPeer);
			disconnectFromError();
			
		} finally {
			disconnect();
		}
		
		Log.i("PeerHandler", "Disconnected from " + mPeer);
	}
	
	/**
	 * Disconnect the Peer from the Torrent, because of an error.
	 */
	public void disconnectFromError() {
		disconnect(true);
	}
	
	/**
	 * Disconnect the Peer from the Torrent.
	 */
	public void disconnect() {
		disconnect(false);
	}
	
	/**
	 * Disconnect from the peer.
	 * @param error An error has occured ?
	 */
	private void disconnect(boolean error) {
		Log.d("PeerHandler", "Disconnected(" + error + ") from " + mPeer);
		
		mActive = false;
		
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				// Nothing to do
			} finally {
				mSocket = null;
				mOutput = null;
				mInput = null;
				mPeer.setSocket(null);
			}
		}
		
		// Remove this PeerHandler from the Torrent
		mTorrent.removePeerHandler(this, error);
	}
	
	/**
	 * Clean all request older than 5 seconds
	 */
	public void cleanOldRequest() {
		synchronized (mPendingRequests) {
			Request r;
			for (int i=0; i<mPendingRequests.size(); i++) {
				r = mPendingRequests.get(i);
				if (r.isOld()) {
					removeRequest(r);
					mPieceManager.removeRequest(r);
					i--;
				}
			}
		}
	}
	
	/**
	 * Send a keepAlive message. Do it approximately every two minutes.
	 */
	private void keepAlive() throws IOException {
		Date now = new Date();

		if (now.after(mNextKeepAlive)) { // Two minutes has been spent ?
			Log.d("KeepAlive", "Sent to " + mPeer);
			mOutput.write(0);	// Send the KeepAlive Message.
			mNextKeepAlive.setTime(now.getTime() + ONE_MINUTE_AND_A_HALF);
		}
	}
	
	/**
	 * Send and receive the Handshake
	 * @return true if the handshake has worked, false otherwise
	 */
	private boolean handshake() throws IOException {
		mOutput.write(new Handshake(mTorrent.getInfoHash(), mTorrent.getPeerID(), mSettings.isEncryptionActivated()));
		
		if (!mAccepted) { // Has the handshake already been received ?
			// Read the answer Handshake from the peer.
			
			Handshake handshake;
			try {
				handshake = mInput.readHandShake();				
			} catch (EOFException e) {
				Log.e("Handshake", "The peer has closed the connexion.");
				return false;
			}
		
			/* Conditions for dropping the connection :
			 * 
			 * If a client receives a handshake with an info_hash that it is not currently serving,
			 * then the client must drop the connection.
			 * If the initiator of the connection receives a handshake in which the peer_id does not match the expected peerid,
			 * then the initiator is expected to drop the connection.
			 */
			if (handshake.getInfoHash().equals(mTorrent.getInfoHash())){
				mPeer.setID(handshake.getPeerId());
				mPeerIsEncrypted = handshake.isEncryptionActivated();
				mPeer.setEncryption(mPeerIsEncrypted);
				return true;
			}
		}
		
		return mAccepted;
	}
	
	/**
	 * Activate the encryption by sending/receiving both RSA and Symmetric Key.
	 */
	private void activateEncryption() throws IOException, MessageException {
		// Create a RSAKeyPair.
		mRSAKeyPair = KeyGenerator.generateRSAKeyPair(mSettings.getEncryptionSettings().getRSAKeylength());
		
		// Create a SymmetricKeyPair.		
		mMySymmetricKeyPair = KeyGenerator.generateXORKeyPair(mSettings.getEncryptionSettings().getSymmetricKeylength());
		
		// Send a SendRSAKey to the client that contains our RSA key.
		mOutput.write(new SendRSAKey(mSettings.getEncryptionSettings().getRSAKeylength(), mRSAKeyPair.getPublicKey(), mRSAKeyPair.getModulo()));
		mOutput.flush();
		
		// If the peer didn't send us a SendRSAKey, we bail out.
		// keyPair constructed with the key get got from the peer (used only to decrypt).
		SendRSAKey rsaKey;
		try {
			rsaKey = (SendRSAKey)mInput.readMessage();
		} catch (ClassCastException e) {
			disconnectFromError();
			return;
		}
		
		RSAKeyPair hisRSAKeyPair = new RSAKeyPair(
				rsaKey.getModulo(),							// Modulo
				rsaKey.getKey(),							// Public Key
				null,										// Private Key (unused here => null)
				rsaKey.getModuloBitCount()						// Bit Count
			);
		
		// Change the regular I/O Streams to their RSA-encrypted versions.
		mInput = new MessageInputStream(new CryptoInputStream(mCounterInput, mRSAKeyPair));
		mOutput = new MessageOutputStream(new CryptoOutputStream(mCounterOutput, hisRSAKeyPair));
		
		// Send the XORKeyPair onto the RSA-encrypted channel...
		mOutput.write(new SendSymmetricKey(mMySymmetricKeyPair.getKey()));
		mOutput.flush();

		// and wait for the answer...
		SendSymmetricKey symmetricKey;
		try {
			symmetricKey = (SendSymmetricKey)mInput.readMessage();
		} catch (ClassCastException e) {
			disconnectFromError();
			return;
		}
		
		XORKeyPair hisSymmetricKeyPair = new XORKeyPair(symmetricKey.getKey());
		
		// Change the regular I/O Streams to their XOR-encrypted versions.
		mInput = new MessageInputStream(new CryptoInputStream(mCounterInput, mMySymmetricKeyPair));
		mOutput = new MessageOutputStream(new CryptoOutputStream(mCounterOutput, hisSymmetricKeyPair));
	}
	
	private void createAndManageRequest() {
		if (mPeerPiecesAvailable == null) return;
		
		Piece piece = mPieceManager.getNeededAndAvailablePiece( mPeerPiecesAvailable );

		if (piece != null) { // The peer doesn't have any interesting piece.
			
			if (!mAmIInterested) {
				synchronized (mMessagesToSendQueue) {
					mMessagesToSendQueue.add(new Interested());
				}
				mAmIInterested = true;
				
			} else if (!mAmIChoked && (mPendingRequests.size() <= 10)) {
				int blockIndex = piece.getLeastRequestedBlockIndex();
				if (blockIndex >= 0) {
					Request request = new Request(piece.getIndex(), piece.getBlockOffset(blockIndex), piece.getBlockSize(blockIndex));
					addRequest(request);
				}
			}
		}
	}
	
	private void updatePeerInformation() {
		mPeer.setPercent( 100.0 * mPeerPiecesAvailable.size() / mTorrent.getPieceCount() );
	}
	
	public void addMessage(Message msg) {
		synchronized (mMessagesToSendQueue) {
			mMessagesToSendQueue.add(msg);
		}
	}
	
	/**
	 * Adds a Block request to the queue.
	 * @param request the request to add to the queue.
	 */
	public void addRequest(Request request) {
		synchronized (mMessagesToSendQueue) {
			mMessagesToSendQueue.add(request);
		}
		synchronized (mPendingRequests) {
			mPendingRequests.add(request);			
		}
		mPieceManager.addRequest(request, this);
	}
	
	/**
	 * Removes a Block request from the queue.
	 * @param request the request to remove from the queue.
	 */
	public void removeRequest(Request request) {
		synchronized (mMessagesToSendQueue) {
			mMessagesToSendQueue.remove(request);
		}
		synchronized (mPendingRequests) {
			mPendingRequests.remove(request);
		}
	}
	
	public void addPeerAvailablePiece(int index) {
		Log.d("PeerHandler", mPeer + " have the piece " + index + "/" + (mTorrent.getPieceCount()-1));
		mPeerPiecesAvailable.add(index);
	}
	
	public boolean equals(Object o) {
		try {
			PeerHandler ph = (PeerHandler)o;
			return mPeer.equals(ph.mPeer);
			
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the number of peers available
	 * @return number of peers available
	 */
	public Set<Integer> getPeerAvailablePieces() {
		return mPeerPiecesAvailable;
	}
	
	/**
	 * Sets the peers that are available
	 * @param set peers available
	 */
	public void setPeerAvailablePieces(Set<Integer> set) {
		if (set != null) { // Assign the set only if it's not null.
			mPeerPiecesAvailable = set;
		}
	}

	/**
	 * Returns true if I am indeed interested, false otherwise.
	 * @return true if I am indeed interested, false otherwise.
	 */
	public boolean amIInterested() {
		return mAmIInterested;
	}

	/**
	 * Set whether or not I am interested
	 * @param value true if I am interested, false if not
	 */
	public void setAmIInterested(boolean value) {
		this.mAmIInterested = value;
	}
	
	/**
	 * Returns true if I am chocked, false otherwise.
	 * @return true if I am chocked, false otherwise.
	 */
	public boolean amIChoked() {
		return mAmIChoked;
	}

	/**
	 * Set whether or not I am chocked
	 * @param value true if I am choked, false if not
	 */
	public void setAmIChoked(boolean value) {
		this.mAmIChoked = value;
	}
	
	/**
	 * Returns true if the peer is interested, false otherwise.
	 * @return true if the peer is interested, false otherwise.
	 */
	public boolean isHeInterested() {
		return mIsHeInterested;
	}

	/**
	 * Set whether or not the peer is interested
	 * @param value true if the peer is interested, false if not
	 */
	public void setIsHeInterested(boolean value) {
		this.mIsHeInterested = value;
	}
	
	/**
	 * Returns true if the peer is chocked, false otherwise.
	 * @return true if the peer is chocked, false otherwise.
	 */
	public boolean isHeChoked() {
		return mIsHeChoked;
	}

	/**
	 * Set whether or not the peer is chocked
	 * @param value true if the peer is choked, false if not
	 */
	public void setIsHeChoked(boolean value) {
		this.mIsHeChoked = value;
	}
	
	/**
	 * Returns the remote peer
	 * @return the remote peer
	 */
	public Peer getPeer() {
		return mPeer;
	}
	
	/**
	 * Returns the torrent contained into this Object
	 * @return the torrent contained into this Object
	 */
	public Torrent getTorrent() {
		return mTorrent;
	}
	
	/**
	 * Returns the average download/upload speed of this peerHandler
	 * @return average download/upload speed of this peerHandler
	 */
	public SpeedPair getSpeed() {
		double download = (mCounterInput == null) ? 0 : mCounterInput.getAverageSpeed();
		double upload = (mCounterOutput == null) ? 0 : mCounterOutput.getAverageSpeed();
		
		return new SpeedPair(download, upload);
	}
}
