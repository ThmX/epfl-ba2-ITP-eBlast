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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import eblast.checksum.Hash;
import eblast.io.SpeedPair;
import eblast.settings.EBlastSettings;
import eblast.settings.EncryptionSettings;
import eblast.settings.XMLException;
import eblast.settings.XMLSettings;
import eblast.torrent.peer.PeerAccepter;

/**
 * This Class, based on the Singleton Pattern, has the ability to manage all torrents.
 * It also can update the settings from an XML file.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 10.05.2011 - Initial release
 * @verison 1.2 - 18.05.2011 - Implementation of speed counter
 */
public class TorrentManager {
	
	// ----- Singleton Pattern -----------------------------------------------
	private static TorrentManager mSingleton = new TorrentManager();
	
	/**
	 * @return Singleton Instance of the TorrentManager
	 */
	public static TorrentManager getInstance() {
		return mSingleton;
	}
	// -----------------------------------------------------------------------
	
	private XMLSettings mXMLSettings;
	
	private List<Torrent> mTorrents;
	private EBlastSettings mSettings;
	private EncryptionSettings mEncryptionSettings;
	private PeerAccepter mPeerAccepter;
	
	// Hides the constructor
	private TorrentManager() {
		mTorrents = new LinkedList<Torrent>();
		mSettings = new EBlastSettings();
		mEncryptionSettings = new EncryptionSettings();
	}
	
	/**
	 * Loads the program settings from the XML file given in parameters.
	 * @param xmlSettings file used to update the program settings
	 * @throws XMLException
	 * @throws IOException
	 */
	public void load(File xmlSettings) throws XMLException, IOException {
		mSettings = new EBlastSettings();
		mXMLSettings = new XMLSettings(mSettings, xmlSettings);
		
		// Because the file exist, we load the settings.
		if (xmlSettings.exists()) {
			mXMLSettings.fromXML();
		} else {
			mXMLSettings.toXML();
		}
		
		// Create the parent directories if they don't exist.
		if (!xmlSettings.getParentFile().exists()) {
			xmlSettings.getParentFile().mkdirs();
		}
		
		mEncryptionSettings = new EncryptionSettings();
		
		relaunch();
	}
	
	/**
	 * Saves the current configuration to an xml file.
	 */
	public void saveToXMLFile() {
		try {
			mXMLSettings.toXML();
		} catch (XMLException e) {}
	}
	
	/**
	 * Relaunches the PeerAccepter instance (if we change the port into the program)
	 * @throws IOException
	 */
	public void relaunch() throws IOException {
		if (mPeerAccepter != null) {
			mPeerAccepter.halt();
		}
		mPeerAccepter = new PeerAccepter(this, mSettings.getPort());
	}
	
	/**
	 * Add a Torrent.
	 * @param torrent torrent to be added.
	 */
	public void addTorrent(Torrent torrent) {
		if (!mTorrents.contains(torrent)) {
			mTorrents.add(torrent);
			torrent.startTorrent();
		}
	}
	
	/**
	 * Get the Torrent mapped by his InfoHash
	 * @param infoHash Hash that represents the torrent
	 * @return Torrent mapped by the key infoHash
	 */
	public Torrent getTorrent(Hash infoHash) {
		for (Torrent t: mTorrents) {
			if (t.getInfoHash().equals(infoHash)) {
				return t;
			}
		}
		return null;
	}
	
	/**
	 * Remove the Torrent mapped by his InfoHash and erase his data if asked.
	 * @param infoHash Hash that represents the torrent
	 * @param erase Do we need to erase all data ?
	 */
	public void removeTorrent(Hash infoHash, boolean erase) {
		Torrent torrent = getTorrent(infoHash);
		torrent.destroyTorrent(erase);
		mTorrents.remove(torrent);
	}
	
	/**
	 * Close all torrents.
	 */
	public void closeTorrents() {
		for (Torrent t: mTorrents) {
			t.stopTorrent();
		}
		mTorrents.clear();
	}
	
	/**
	 * Get a Collection of Torrents
	 * @return Collection that contains all torrent from the TorrentManager
	 */
	public Collection<Torrent> getTorrents() {
		return mTorrents;
	}
	
	/************************ GETTERS / SETTERS *************************/

	/**
	 * Returns the current settings
	 * @return current settings
	 */
	public EBlastSettings getSettings() {
		return mSettings;
	}
	
	/**
	 * updates the current encryption settings with the object given in parameters
	 * @param settings new encryption configuration
	 */
	public void setEncryptionSettings(EncryptionSettings settings) {
		mEncryptionSettings = settings;
	}
	
	/**
	 * Returns the current encryption configuration
	 * @return current encryption configuration
	 */
	public EncryptionSettings getEncryptionSettings() {
		return mEncryptionSettings;
	}
	
	/**
	 * Returns the average speed of all torrents
	 * @return average speed of all torrents
	 */
	public SpeedPair getSpeed() {
		double download = 0;
		double upload = 0;
		
		for (Torrent t: mTorrents) {
			SpeedPair speed = t.getSpeed();
			download += speed.download;
			upload += speed.upload;
		}
		
		return new SpeedPair(download, upload);
	}
	
	/**
	 * Returns the hash of the whole list of torrents contained in this class.
	 * It is useful for knowing if the list has changed (an element more or less). 
	 * @return
	 */
	public int getTorrentListHash() {
		
		return mTorrents.hashCode();
	}

	/**
	 * Returns the number of torrents contained in this TM.
	 * @return number of torrents contained in this TM.
	 */
	public int getNumberOfTorrents() {
		return mTorrents.size();
	}
}
