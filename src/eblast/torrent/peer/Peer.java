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

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import eblast.log.Log;
import eblast.torrent.Torrent;

/**
 * This class represents one of the Peer of the Torrent. 
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 0.1 - 07.03.2011 - Initial version
 */
public class Peer {
	
	private InetAddress mIP;
	private int mPort;
	private Torrent mTorrent;
	private String mID;

	private double mPercent; // Percentage of available and interesting data that the peer posess
	private boolean mEncrypted;
	private Socket mSocket;
	
	/**
	 * This constructor initialize a peer from all informations needed.
	 * @param ip Array of byte representing the IP Address.
	 * @param port Port number to connect to this peer.
	 * @param torrent Torrent that the peer represents.
	 * @throws UnknownHostException Throw this exception if the host is unknown.
	 */
	public Peer(byte[] ip, int port, Torrent torrent) throws UnknownHostException {
		this(InetAddress.getByAddress(ip), port, torrent);
	}
	
	/**
	 * This constructor initialize a peer from all informations needed.
	 * @param ip InetAddress representing the IP Address.
	 * @param port Port number to connect to this peer.
	 * @param mTorrent Torrent that the peer represents.
	 */
	public Peer(InetAddress ip, int port, Torrent torrent) {
		mTorrent = torrent;
		
		mIP = ip;
		mPort = port;
		
		mID = "";
		mSocket = null;
		mEncrypted = false;
		mPercent = 0;
		
		Log.i("new Peer", this + " on torrent \"" + mTorrent + "\"");
	}

	/**
	 * Check if two peers are the same.
	 * @param p Peer to check
	 * @return True if it is, false otherwise.
	 */
	public boolean equals(Object o) {
		try {
			Peer p = (Peer)o;
			return mIP.equals(p.mIP) && (mPort == p.mPort) && mTorrent.equals(p.mTorrent);
			
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the address of the peer : <address> : <port>
	 * @return Addresss of the peer
	 */
	public String toString() {
		return mIP.getHostAddress() + ":" + mPort;
	}
	
	/**
	 * @return InetAddress representing the IP Address of the peer.
	 */
	public InetAddress getIP() {
		return mIP;
	}

	/**
	 * @return Port to connect to this peer.
	 */
	public int getPort() {
		return mPort;
	}
	
	/**
	 * Set the socket given in parameter as the main socket
	 * @param socket socket to use as the main socket
	 */
	public void setSocket(Socket socket) {
		mSocket = socket;
	}
	
	/**
	 * Returns the socket of the peer
	 * @return socket of the peer
	 */
	public Socket getSocket(){
		return mSocket;
	}
	
	/**
	 * Activates/Desactivates encryption with the boolean given
	 * in parameter (true activates it, false desactivates it) 
	 * @param activated if true, activates encryption. Desactivates it if false
	 */
	public void setEncryption(boolean activated) {
		mEncrypted = activated;
	}
	
	/**
	 * Returns true if we are in encrypted-mode, false otherwise.
	 * @return true if we are in encrypted-mode, false otherwise.
	 */
	public boolean isEncrypted() {
		return mEncrypted;
	}
	
	/**
	 * Set the peer id to the value given in parameter
	 * @param id new peer id
	 */
	public void setID(String id) {
		mID = id;
	}

	/**
	 * Returns the current peer id
	 * @return the current peer id
	 */
	public String getID() {
		return mID;
	}
	
	/**
	 * Updates the percentage of available and interesting data that the peer posess
	 * @param percent percentage of available and interesting data that the peer posess
	 */
	public void setPercent(double percent) {
		mPercent = percent;
	}
	
	/**
	 * Returns the percentage of available and interesting data that the peer posess
	 * @return percentage of available and interesting data that the peer posess
	 */
	public double getPercent() {
		return mPercent;
	}
}
