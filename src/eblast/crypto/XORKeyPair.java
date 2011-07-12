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

package eblast.crypto;

import java.math.BigInteger;
import java.util.Arrays;

import eblast.log.Log;

/**
 * This class allows the user to encrypt and decrypt a message
 * using the symmetric exclusive OR (XOR) method.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 26.02.2011 - Initial version
 */
public final class XORKeyPair extends KeyPair {
	
	// Constants
	private static final int ENCRYPT_INDEX = 0;
	private static final int DECRYPT_INDEX = 1;
	
	private byte[] mKey; // Symmetric key.
	
	/* 
	 * Since we are encrypting and decrypting in several steps ( we're not getting
	 * the messages to encrypt/decrypt at once ) we need to keep different indexes
	 * for, respectively, encryption and decryption, therefore we created this mKeyIndices.
	 */
	private int[] mKeyIndices; 
	
	/**
	 * Default constructor.
	 * @param key symmetric key (into a byte array) that is going to be used to encrypt/decrypt data
	 */
	public XORKeyPair(byte[] key) {
		
		super(key.length, 1);
		
		mKey = key;
		mKeyIndices = new int[2]; // Encrypt & Decrypt indexes
		
		// Debug
		Log.d("SymmetricKeyPair", "(" + mKey.length + ")" + Arrays.toString(mKey));
	}
	
	/**
	 * Encrypt the given data using the XOR method.
	 * @param data message to encrypt.
	 * @param index index of the keyIndex to use (depends if we want to encrypt or decrypt).
	 * @return the encrypted message.
	 */
	private byte[] encrypt(byte[] data, int index) {
		
		byte[] encryptedMsg = new byte[data.length]; // encrypted message to be returned.
		int idx = mKeyIndices[index]; // extract the current index.
		
		for (int i = 0; i < data.length; i++) { // Every bytes of the given message.
			
			idx %= mKey.length; // This will "align" the message under the key if the message is longer than the key.
			encryptedMsg[i] = (byte) (data[i] ^ mKey[idx++]); // bitwise XOR between the data byte and the key.
		}
		
		mKeyIndices[index] = idx; // store the key index.
		
		return encryptedMsg;
	}
	
	/**
	 * Encrypt the given data using the XOR method.
	 * @param data message to encrypt.
	 * @return the encrypted message.
	 */
	public byte[] encrypt(byte[] data) {
				
		if (mKey == null) {
			Log.e("XOR symmetric key error", "The symmetric key is not defined.");
			throw new NullPointerException("The  key is not defined.");
		}
		
		return encrypt(data, ENCRYPT_INDEX);
	}
	
	/**
	 * Encrypt the given data using the XOR method.
	 * @param BigInteger to encrypt.
	 * @return the encrypted message.
	 */
	public BigInteger encrypt(BigInteger data) {
		return new BigInteger(encrypt(data.toByteArray()));
	}

	/**
	 * Decrypt the given encrypted data using the XOR method.
	 * @param data encrypted message to be decrypted.
	 * @return the original message.
	 */
	public byte[] decrypt(byte[] data) {
		
		return encrypt(data, DECRYPT_INDEX);
	}
	
	/**
	 * Decrypt the given encrypted data using the XOR method.
	 * @param data encrypted message to be decrypted.
	 * @return the original message.
	 */
	public BigInteger decrypt(BigInteger data) {
		return new BigInteger(decrypt(data.toByteArray()));
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * returns the symmetric key.
	 * @returns array of bytes containing the symmetric key.
	 */
	public byte[] getKey() {
		return mKey;
	}
}
