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

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import eblast.checksum.Hash;
import eblast.log.Log;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentManager;
import eblast.torrent.messages.Handshake;
import eblast.torrent.messages.MessageInputStream;

/**
 * This class is used to add Peers
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 0.1 - 12.04.2011 - Initial version
 */
public class PeerAccepter extends Thread {
	
	private ServerSocket mServerSocket;
	private TorrentManager mTorrentManager;
	
	/**
	 * Default constructor
	 * @param torrentManager torrentManager
	 * @param port port used for the connection
	 * @throws IOException
	 */
	public PeerAccepter(TorrentManager torrentManager, int port) throws IOException {
		super("PeerAccepter");
		
		mTorrentManager = torrentManager;

		mServerSocket = null;
		while (mServerSocket == null) { // Tries to initiate the serverSocket
			try {
				mServerSocket = new ServerSocket(port);
			} catch (BindException e) {
				e.printStackTrace();
				port++;
			}
		}

		Log.i("PeerAccepter", "Launch on port " + port);
		start();
	}
	
	/**
	 * Stops the PeerAccepter.
	 */
	public void halt() {
		interrupt();
		try {
			mServerSocket.close();
		} catch (IOException e) {
			Log.e("PeerAccepter", "Closing the server socket.");
		}
	}
	
	/**
	 * Main procedure of peerAccepter.
	 */
	public void run() {
		while (true) {
			try {
				Socket socket = mServerSocket.accept(); // Wait for a new peer
				Log.i("PeerAccepter", "New Peer connected ->");
				
				MessageInputStream mis = new MessageInputStream(socket.getInputStream());
				Handshake handshake = mis.readHandShake();
				
				Hash infoHash = handshake.getInfoHash();
				
				Torrent torrent = mTorrentManager.getTorrent(infoHash);
				
				// If the torrent isn't in the TorrentManager's list
				if (torrent == null) {
					socket.close();
					
				} else {
					
					// Create the peer and put it into the Torrent Peers' List
					Peer peer = new Peer(socket.getInetAddress(), socket.getPort(), torrent);
					peer.setID(handshake.getPeerId());
					peer.setSocket(socket);
					peer.setEncryption(handshake.isEncryptionActivated());
					torrent.addPeer(peer);
				}
				
			} catch (BindException e) {
				Log.e("PeerAccepter", "Bind Error, " + e.getMessage());
				
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("PeerAccepter", "Error while connecting with a peer.");
			}
			
			try {
				sleep(100L);
			} catch (InterruptedException e) {
				Log.e("PeerAccepter", "Thread.sleep()");
			}
		}
	}
}
