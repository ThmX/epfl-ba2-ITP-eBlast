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

package eblast.torrent.tracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import eblast.Convertor;
import eblast.bencoding.BDecoder;
import eblast.bencoding.BEDictionary;
import eblast.http.HTTPGet;
import eblast.log.Log;
import eblast.torrent.Torrent;
import eblast.torrent.peer.Peer;

/**
 * Modelizes a tracker and some functions related to the tracker,
 * such as retrieve a list of peers and create an AnnounceInfo.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 08.03.2011 - Initial version.
 * @version 1.1 - 15.03.2011 - Add unimplemented keys and fix InetAddress + convert IP+Port +
 * Log updates + tracker response is now stored with a dictionnary and given to the AnnounceInfo as such.
 */
public class TrackerInfo {
	
	public static final String PREFIX_HTTP			= "http://";
	public static final String PREFIX_UDP			= "udp://";
	
	private static final String DEFAULT_STATUS		= "ok";

	/**
	 * Keywords to send to the tracker.
	 */
	public static final String KEY_INFO_HASH		= "info_hash";
	public static final String KEY_PEER_ID			= "peer_id";
	public static final String KEY_PORT				= "port";
	public static final String KEY_LEFT				= "left";
	public static final String KEY_COMPACT			= "compact";
	public static final String KEY_EVENT			= "event";
	public static final String KEY_EVENT_STARTED	= "started";
	public static final String KEY_EVENT_COMPLETED	= "completed";
	public static final String KEY_EVENT_STOPPED	= "stopped";
	public static final String KEY_TRACKERID		= "trackerid";
	public static final String KEY_NUMWANT			= "numwant";
	
	public static final String KEY_UPLOADED			= "uploaded";
	public static final String KEY_DOWNLOADED		= "downloaded";
	public static final String KEY_KEY				= "key";
	
	/**
	 * Keywords received by the tracker.
	 */
	public static final String KEY_INTERVAL			= "interval";
	public static final String KEY_MIN_INTERVAL		= "min interval";
	public static final String KEY_FAILURE_REASON	= "failure reason";
	public static final String KEY_COMPLETE			= "complete";
	public static final String KEY_INCOMPLETE		= "incomplete";
	public static final String KEY_PEERS			= "peers";
	
	private static final String[] NEEDED_PARAMETERS = {KEY_INFO_HASH, KEY_PEER_ID, KEY_PORT, KEY_LEFT, KEY_COMPACT, KEY_EVENT};
	
	private Torrent mTorrent;
	private String mAddress;
	private String mTrackerID;
	private String mStatus;
	private long mLastUpdate;
	private int mSeedersNumber;
	private int mLeechersNumber;
	
	/**
	 * Default constructor
	 * @param torrent instance of the current torrent that we use for the tracker.
	 * @param address IP address of the Tracker.
	 */
	public TrackerInfo(Torrent torrent, String address) throws TrackerInfoException {
		
		// XXX When UDP will be implemented
		//if (!address.startsWith(PREFIX_HTTP) || !address.startsWith(PREFIX_UDP)) {
		if (!address.startsWith(PREFIX_HTTP)) {
			throw new TrackerInfoException("Tracker " + address + " not supported");
		}
		
		mTorrent = torrent;
		mAddress = address;
		
		Log.i("Tracker created", mAddress.toString());
	}
	
	/**
	 * Generate and return an AnnounceInfo from a Tracker for the torrent given in parameters.
	 * @param parameters contains all the commands (key) and the content of the commands (value)
	 * that we want to send to the tracker in our GET request.
	 * @return the AnnounceInfo computed from the given parameters.
	 * @throws TrackerInfoException 
	 */
	public AnnounceInfo getAnnounceInfo(Map<String, String> parameters) throws TrackerInfoException {
		
		AnnounceInfo currentAnnounceInfo = null; // Instance to return.
		
		// Check if the given parameters are correct.
		for (String p: NEEDED_PARAMETERS) {
			if (!parameters.containsKey(p)){
				Log.e("", "the key " + p + " is missing.");
				throw new TrackerInfoRequiredKeyException(p);
			}
			
			// Prints the key and its value.
			Log.i(p, parameters.get(p));
		}

		if (mTrackerID != null) // If there is no trackerId parameter, we add it (due to tracker restrictions).
			parameters.put(KEY_TRACKERID, mTrackerID);
		
		InputStream trackerStream = null;		
		try {
			trackerStream = HTTPGet.openURLStream(mAddress, parameters); // Do the request to the tracker.
			
			BDecoder decoder = new BDecoder(trackerStream);
			
			// Get all the BEValues from the previous BEValue and put it into a wrapper
			// that allows us to access to the data inside the BEValues by its methods.
			BEDictionary dictionary = new BEDictionary(decoder.bdecodeMap());
			
			// If there has been a problem during the request, throws an exception.
			if (dictionary.contains(KEY_FAILURE_REASON)) {
				mStatus = dictionary.getString(KEY_FAILURE_REASON);
				throw new TrackerInfoException(mStatus);

			} else {
				mStatus = DEFAULT_STATUS; // Remove error status
				
				if (dictionary.contains(KEY_TRACKERID)) { // Optional answer from the tracker.
					mTrackerID = dictionary.getString(KEY_TRACKERID);
				}
				
				if (dictionary.contains(KEY_COMPLETE)) { // Optional answer from the tracker.
					mSeedersNumber = dictionary.getInt(KEY_COMPLETE);
				}
				
				if (dictionary.contains(KEY_INCOMPLETE)) { // Optional answer from the tracker.
					mLeechersNumber = dictionary.getInt(KEY_INCOMPLETE);
				}
				
				byte[] peersName = dictionary.getBytes(KEY_PEERS); // Get the peers name list from the tracker's response.
				
				ArrayList<Peer> peerList = new ArrayList<Peer>();
				for (int i=0; i<peersName.length; i+=6) { // Creation of peers from the name list.
					
					// We separate peers' ip and port.
					byte[] ip = Arrays.copyOfRange(peersName, i, i+4);
					int port = Convertor.toInteger(Arrays.copyOfRange(peersName, i+4, i+6));
					
					// Add a new peer to our list.
					peerList.add(new Peer(ip, port, mTorrent));
				}
				
				// Create the AnnounceInfo that has to be returned.
				currentAnnounceInfo = new AnnounceInfo(dictionary, peerList);
			}
			
		} catch (IOException e) {
			throw new TrackerInfoException("Error during the connection with the tracker " + mAddress);
			
		} finally{
			try {
				if (trackerStream != null)
					trackerStream.close(); // We close the stream, whatever happens.
			} catch (IOException e) {}
		}
		
		mLastUpdate = new Date().getTime();
		return currentAnnounceInfo;
	}
	
	/**
	 * Returns true if the delay is above 5 seconds, false otherwise.
	 * @param delay delay to test.
	 * @return true if the delay is above 5 seconds, false otherwise
	 */
	public boolean needToBeUpdated(long delay) {
		Date now = new Date();
		Date nextUpdate = new Date(mLastUpdate+delay);
		return now.after(nextUpdate);
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		
		try {
			TrackerInfo ti = (TrackerInfo)o;
			return mAddress.equals(ti.mAddress);
			
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return mAddress;
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * @return Status of the Error, null if there isn't any errors.
	 */
	public String getErrorStatus() {
		return mStatus;
	}
	
	/**
	 * @return when the last update has been made.
	 */
	public long getLastUpdate() {
		return mLastUpdate;
	}

	/**
	 * @return number of seeders
	 */
	public int getSeedersNumber() {
		return mSeedersNumber;
	}

	/**
	 * @return number of leechers
	 */
	public int getLeechersNumber() {
		return mLeechersNumber;
	}
}
