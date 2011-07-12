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

package eblast.checksum;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import eblast.Convertor;
import eblast.http.BinaryURLEncoder;

/**
 * This class is an immutable class containing the Hash of a Checksum.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 * @version 1.1 - Fix bug about HexString to Bytes
 */
public class Hash {
	private byte[] mHash;
	private String mHexHash;
	private String mURLHash;
	
	/**
	 * Initializes the Hash from an Array of Bytes.
	 * @param hash Array of Bytes representing the Hash.
	 * @throws UnsupportedEncodingException 
	 */
	public Hash(byte[] hash) throws NullHashException {
		if (hash == null) throw new NullHashException();
		
		mHash = Arrays.copyOf(hash, hash.length);
		mHexHash = Convertor.toHexString(hash);
		
		mURLHash = BinaryURLEncoder.encode(mHash);
	}
		
	/**
	 * Initialize the Hash from a Hexadecimal String.
	 * @param hash String representing the Hash in Hexadecimal.
	 */
	public Hash(String hash) throws NullHashException, UnsupportedEncodingException {
		if (hash == null) throw new NullHashException();
		
		mHexHash = hash;
		mHash = Convertor.toBytes(mHexHash);
		mURLHash = URLEncoder.encode(String.valueOf(mHash), "UTF-8");
	}
	
	/**
	 * Compare the hash and return whether their equals or not.
	 * @param hash Hash to be compared.
	 * @return True if their are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		try {
			return mHexHash.equals(((Hash) obj).mHexHash);

		} catch (ClassCastException e) { // If the class is not the same, then it is not equal.
			return false;
		}
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * @return Array of Bytes representing the Hash.
	 */
	public byte[] toBytes() {
		return Arrays.copyOf(mHash, mHash.length);
	}
	
	/**
	 * @return Array of Bytes represented in a String
	 */
	public String toURLString() {
		return mURLHash;
	}
	
	/**
	 * @return Hexadecimal String representing the Hash.
	 */
	public String toHexString() {
		return mHexHash;
	}
}
