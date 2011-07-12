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

import java.util.ArrayList;
import java.util.List;

import eblast.bencoding.BEDictionary;
import eblast.bencoding.InvalidBEncodingException;
import eblast.torrent.peer.Peer;

/**
 * This class modelizes the response from the Tracker.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 08.03.2011 Initial version.
 * @version 1.1 - 15.03.2011 New Constructor with a dictionnary instead of named parameters.
 */
public class AnnounceInfo {
	
	private int mInterval;
	private int mMinInterval;
	private String mFailureReason;
	private boolean mFailed;
	private int mComplete;
	private int mIncomplete;
	private List<Peer> mPeersList;
	
	/**
	 * Simplified constructor in case of an error.
	 * When the tracker answers by an error, it does not send anything else.
	 * @param failureReason Human-readable message of the error returned by the tracker.
	 */
	public AnnounceInfo(String failureReason) {
		mFailureReason = failureReason;
		mFailed = true;
	}
	
	/**
	 * Default constructor, uses a dictionnary given in parameter that contains all the keys returned
	 * by the Tracker.
	 * @param dict dictionnary containing all the keys returned by the tracker.
	 * @param peerList List of peers.
	 * @throws InvalidBEncodingException
	 * @throws TrackerInfoRequiredKeyException
	 */
	public AnnounceInfo(BEDictionary dict, ArrayList<Peer> peerList) throws InvalidBEncodingException, TrackerInfoRequiredKeyException {
		mPeersList = peerList;
		
		// ----- Required Keys -----------------------------------------------
		if (dict.contains(TrackerInfo.KEY_INTERVAL))
			mInterval = dict.getInt(TrackerInfo.KEY_INTERVAL);
		else
			throw new TrackerInfoRequiredKeyException(TrackerInfo.KEY_INTERVAL);
		// -------------------------------------------------------------------
		
		// ----- Optional keys -----------------------------------------------
		if (dict.contains(TrackerInfo.KEY_MIN_INTERVAL))
			mMinInterval = dict.getInt(TrackerInfo.KEY_MIN_INTERVAL);
		
		if (dict.contains(TrackerInfo.KEY_FAILURE_REASON))
			mFailureReason = dict.getString(TrackerInfo.KEY_FAILURE_REASON);
		
		if (dict.contains(TrackerInfo.KEY_COMPLETE))
			mComplete = dict.getInt(TrackerInfo.KEY_COMPLETE);
		
		if (dict.contains(TrackerInfo.KEY_INCOMPLETE))
			mIncomplete = dict.getInt(TrackerInfo.KEY_COMPLETE);
		// -------------------------------------------------------------------
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * @return True if it has failed, false otherwise.
	 */
	public boolean hasFailed() {
		return mFailed;
	}

	/**
	 * @return the current interval
	 */
	public int getInterval() {
		return mInterval;
	}

	/**
	 * @return the minimum interval
	 */
	public int getMinInterval() {
		return mMinInterval;
	}

	/**
	 * @return The reason why it failed
	 */
	public String getFailureReason() {
		return mFailureReason;
	}
	
	/**
	 * @return number of seeders
	 */
	public int getComplete() {
		return mComplete;
	}

	/**
	 * @return @return number of leechers
	 */
	public int getIncomplete() {
		return mIncomplete;
	}

	/**
	 * 
	 * @return List of peers received from the tracker.
	 */
	public List<Peer> getPeersList() {
		return mPeersList;
	}
}
