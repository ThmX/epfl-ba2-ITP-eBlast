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

/**
 * Contains all the settings related to a peer,
 * such as the length of RSA/XOR keys or if 
 * we activated data encryption.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class EncryptionSettings {
	
	//--------------- Constants --------------------
	
	public static final int DEFAULT_RSA_KEYLENGTH = 128;
	public static final int DEFAULT_SYMMETRIC_KEYLENGTH = 128;
	
	//----------------------------------------------

	// Encryption
	private int mRSAModlength 			= DEFAULT_RSA_KEYLENGTH;
	private int mSymmetricKeylength 	= DEFAULT_SYMMETRIC_KEYLENGTH;

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Return the length of the RSA modulo in number of bits.
	 * @return length of the RSA modulo in number of bits
	 */
	public int getRSAKeylength() {
		return mRSAModlength;
	}

	/**
	 * Return the length of the symmetric key in bytes.
	 * @return length of the symmetric key in bytes
	 */
	public int getSymmetricKeylength() {
		return mSymmetricKeylength;
	}
	
	/**
	 * Enable encryption (using either RSA or XOR, or both)
	 * @param RSAModlength length of the RSA modulo in number of bits
	 * @param symmetricKeylength length of the symmetric key in bytes
	 */
	public void setEncryptionSettings(int RSAModlength, int symmetricKeylength) {
		mRSAModlength = RSAModlength;
		mSymmetricKeylength = symmetricKeylength;
	}
}
