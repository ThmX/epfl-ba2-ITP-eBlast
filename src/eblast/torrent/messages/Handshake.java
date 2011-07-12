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

package eblast.torrent.messages;

import eblast.checksum.Hash;
import eblast.log.Log;

/**
 * This class represents the Handshake message.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class Handshake {

	// Constants
	public static final String DEFAULT_PSTR = "BitTorrent protocol"; // Bittorrent Protocol 1.0
	public static final byte RESERVED_LENGTH = 8;
	public static final byte INFO_HASH_LENGTH = 20;
	public static final byte PEER_ID_LENGTH = 20;
	private static final int BIT_ENCRYPTION = 60;
	
	// Attributes
	private String mPstr;
	private byte[] mReserved;
	private Hash mInfoHash;
	private String mPeerId;
	
	/**
	 * Default constructor.
	 * @param infoHash 20-byte SHA1 hash of the info key in the metainfo file.
	 * This is the same info_hash that is transmitted in tracker requests.
	 * @param peerID 20-byte string used as a unique ID for the client.
	 * @param reserved eight (8) reserved bytes. All current implementations use all zeroes.
	 * Each bit in these bytes can be used to change the behavior of the protocol.
	 */
	public Handshake(Hash infoHash, String peerID, byte[] reserved) {
		
		mPstr = DEFAULT_PSTR;
		
		mReserved = reserved;
		
		mInfoHash = infoHash;
		mPeerId = peerID;
		
		Log.d("Handshake", "PStr=" + mPstr + " - InfoHash=" + mInfoHash.toURLString() + " - PeerID=" + mPeerId);
	}
	
	/**
	 * @param infoHash 20-byte SHA1 hash of the info key in the metainfo file.
	 * This is the same info_hash that is transmitted in tracker requests.
	 * @param peerID 20-byte string used as a unique ID for the client.
	 * @param encrypted true if are in encrypted mode, false otherwise
	 */
	public Handshake(Hash infoHash, String peerId, boolean encrypted) {
		this(infoHash, peerId, new byte[8]);
		if (encrypted) {
			activate(BIT_ENCRYPTION);
		}
	}
	
	/**
	 * Set the "<code>bit</code>-ieth" bit to 1, where <code>bit</code>
	 * is an index given in parameter.
	 * @param bitIndex index of the bit we want to set to 1.
	 */
	private void activate(int bitIndex) {
		mReserved[bitIndex / 8] |= (byte) (1 << (bitIndex % 8));
	}
	
	/**
	 * Returns true if the bit at the given index is equals to 1, false otherwise.
	 * @param bitIndex index of a bit.
	 * @return true if the bit at the given index is equals to 1, false otherwise
	 */
	private boolean isActivated(int bitIndex) {
		return (mReserved[bitIndex / 8] & (1 << (bitIndex % 8))) == (1 << (bitIndex % 8));
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Test whether the encryption is activated.
	 * @return true if encryption is activated, false otherwise.
	 */
	public boolean isEncryptionActivated() {
		return isActivated(BIT_ENCRYPTION);
	}
	
	/**
	 * Returns the reserved bytes (used for encryption)
	 * @return reserved bytes (used for encryption)
	 */
	public byte[] getReserved() {
		return mReserved;
	}

	/**
	 * Returns the infoHash of the torrent
	 * @return infoHash of the torrent
	 */
	public Hash getInfoHash() {
		return mInfoHash;
	}

	/**
	 * Returns our peerId
	 * @return our peerId
	 */
	public String getPeerId() {
		return mPeerId;
	}
}
