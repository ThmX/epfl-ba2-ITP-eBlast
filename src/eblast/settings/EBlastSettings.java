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

package eblast.settings;

import java.io.File;
import java.util.Random;

/**
 * This class contains all the parameters of the program, such as
 * port, download folder, encryption, etc.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class EBlastSettings {
	
	//--------------- Constants --------------------
	
	// File separator (Platform-specific)
	private static final String FILE_SEP = System.getProperty("file.separator");
	
	// Download directory
	private static final String DEFAULT_DIR = System.getProperty("user.home") + FILE_SEP + "Downloads" + FILE_SEP;
	
	// Default port
	private static final int DEFAULT_PORT = (new Random()).nextInt(30000) + 6881;
	
	// Maximum number of peers
	private static final int DEFAULT_MAX_PEERS = 60;

	//--------------- Constants --------------------
	
	private EncryptionSettings mEncryptionSettings = new EncryptionSettings();
	
	// Encryption
	private boolean mEncrypted = false;
	private boolean mIgnoreUnencrypted = false; // Only encrypted mode
	
	// Actual attributes of the class
	private File mDownloadDir = new File(DEFAULT_DIR);
	private int mPort = DEFAULT_PORT;
	private int mMaxPeers = DEFAULT_MAX_PEERS;
	
	/**
	 * Create an XML String with all the parameters above. This String
	 * is supposed to be written into XMLSettings (XML parser).
	 * @return a String that contains all the parameters given in this class.
	 */
	public String toXML() {
		
		final String endl = System.getProperty("line.separator"); // Platform-specific line separator
		
		// Construct the String using a StringBuilder.
		StringBuilder builder = new StringBuilder();
		builder.append("<eblast port=\"" + mPort + "\" maxpeers=\"" + mMaxPeers + "\" ");
		builder.append("encrypted=\"" + mEncrypted + "\" ignoreunencrypted=\"" + mIgnoreUnencrypted + "\">" + endl);
		builder.append("\t<download path=\"" + mDownloadDir.getAbsolutePath().trim() + "\" />" + endl);
		builder.append("</eblast>");
		
		
		return builder.toString();
	}
	
	/************************ GETTERS / SETTERS *************************/

	/**
	 * Returns the download directory.
	 * @returns the download directory
	 */
	public File getDownloadDir() {
		return mDownloadDir;
	}
	
	/**
	 * Set the download directory.
	 * @param new download directory that we want for our torrents
	 */
	public void setDownloadDir(File file) {
		mDownloadDir = file;
	}
	
	/**
	 * Returns the port used by the program to receive data.
	 * @return port used by the program to receive data
	 */
	public int getPort() {
		return mPort;
	}
	
	/**
	 * Set the port we want to use to receive data.
	 * @param port new port that we want to use to receive data
	 */
	public void setPort(int port) {
		mPort = port;
	}
	
	/**
	 * Returns the maximum number of peers allowed by this program.
	 * @returnmaximum number of peers allowed by this program
	 */
	public int getMaxPeers() {
		return mMaxPeers;
	}
	
	/**
	 * Sets the maximum number of peers allowed by this program.
	 * @param max the maximum number of peers allowed by this program
	 */
	public void setMaxPeers(int max) {
		mMaxPeers = max;
	}
	
	public EncryptionSettings getEncryptionSettings() {
		return mEncryptionSettings;
	}
	
	/**
	 * Returns true if the encryption is activated, false otherwise.
	 * @return true if the encryption is activated, false otherwise
	 */
	public boolean isEncryptionActivated() {
		return mEncrypted;
	}
	
	/**
	 * Uses encryption if the parameter is true, do not use it otherwise,
	 * @param activate parameter used to activate/desactivate encryption.
	 */
	public void setEncryption(boolean activate) {
		mEncrypted = activate;
	}

	/**
	 * Returns true if we are in encrypted-only mode, false otherwise.
	 * @return true if we are in encrypted-only mode, false otherwise
	 */
	public boolean isIgnoringUnencrypted() {
		return mIgnoreUnencrypted;
	}
	
	/**
	 * Go to encrypted-only mode if the parameter is true, accept non-encrypted data otherwise.
	 * @param activate if true, we go to encrypted-only mode. Otherwise we accept non-encrypted data
	 */
	public void setIgnoreUnencrypted(boolean activate) {
		mIgnoreUnencrypted = activate;
	}
}