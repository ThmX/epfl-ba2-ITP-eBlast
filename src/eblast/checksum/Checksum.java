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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eblast.log.Log;

/**
 * This class allows the user to compute a checksum.<br>
 * You <b>HAVE TO</b> use {@link initChecksum()} to initiate the class.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.02.2011 - Initial version
 * @version 1.1 - 27.02.2011 - Add NoSuchAlgorithmException
 */
public final class Checksum {

	private static final String ALGORITHM_MD5 = "MD5";
	private static final String ALGORITHM_SHA1 = "SHA-1";
	
	private MessageDigest mDigest;
	
	/**
	 * Initialize the Checksum Class.
	 * @param algorithm name of the requested algorithm.
	 * @throws NoSuchAlgorithmException If the system does not manage the algorithm.
	 */
	private Checksum(String algorithm) throws NoSuchAlgorithmException {
		mDigest = MessageDigest.getInstance(algorithm);
	}

	/**
	 * Add data into the buffer before computation, if the data is null, nothing will append.
	 * @param data data to be digested. 
	 */
	public void append(byte[] data) {
		if (data != null)
			mDigest.update(data);
	}
	
	/**
	 * Compute the hash, reinitialize the Checksum after the command.
	 * @return A Hash Object containing the Hash Digest.
	 * @throws UnsupportedEncodingException 
	 * @throws NullHashException 
	 */
	public Hash digest() throws NullHashException, UnsupportedEncodingException {
		return new Hash(mDigest.digest());
	}
	
	/**
	 * Compute the hash of the data in arguments.
	 * @param Array of Bytes contains the data to be hashed.
	 * @return A Hash Object containing the Hash Digest.
	 * @throws UnsupportedEncodingException 
	 * @throws NullHashException 
	 */
	public Hash digest(byte[] data) throws NullHashException, UnsupportedEncodingException {
		mDigest.reset();
		return new Hash(mDigest.digest(data));
	}
	
	/**
	 * Resets the digest for further use.
	 */
	public void reset() {
	    mDigest.reset();
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Get the CheckSum Digest in MD5 mode.
	 * @return the Checksum MD5 Instance, null if an error has occured.
	 * @throws NoSuchAlgorithmException 
	 */
	public static Checksum getMD5Instance() throws NoSuchAlgorithmException {
		try {
			return new Checksum(ALGORITHM_MD5);
		} catch (NoSuchAlgorithmException e) {
			Log.e("Checksum::NoSuchAlgorithmException", "Your system doesn't manage the MD5 algorithm");
			throw new NoSuchAlgorithmException("Your system doesn't manage the MD5 algorithm");
		}
	}
	
	/**
	 * Get the CheckSum Digest in SHA-1 mode.
	 * @return the Checksum SHA-1 Instance, null if an error has occured.
	 * @throws NoSuchAlgorithmException 
	 */
	public static Checksum getSHA1Instance() throws NoSuchAlgorithmException {
		try {
			return new Checksum(ALGORITHM_SHA1);
		} catch (NoSuchAlgorithmException e) {
			Log.e("Checksum::NoSuchAlgorithmException", "Your system doesn't manage the SHA-1 algorithm");
			throw new NoSuchAlgorithmException("Your system doesn't manage the SHA-1 algorithm");
		}
	}
}