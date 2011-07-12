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
 * This class allows the user to use RSA encryption/decryption.
 * This class contains the necessary methods to encrypt and decrypt
 * a given message.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.02.2011 - Initial version
 * @version 1.1 - 20.05.2011 - Algorithm corrections for byte array (encrypt and decrypt)
 * @version 1.2 - 24.05.2011 - Algorithm modification for encryption (more efficient)
 */
public final class RSAKeyPair extends KeyPair {
	
	// Constants.
	public static final BigInteger BI_ZERO = new BigInteger("0");
	public static final BigInteger BI_ONE  = new BigInteger("1");
	public static final BigInteger BI_TWO  = new BigInteger("2");
	
	// RSA-specific variables needed for computation.
	private BigInteger mMod;
	private BigInteger mPublicKey;
	private BigInteger mPrivateKey;
	
	/**
	 * Default constructor. Used for encryption and decryption
	 * @param mod Modulo
	 * @param publicKey public Key
	 * @param privateKey private Key
	 * @param publicKeyBitCount number of bits into the public key
	 * @throws IllegalArgumentException throws an exception if <code>publicKeyBitCount</code> isn't a multiple of 8
	 */
	public RSAKeyPair(BigInteger mod, BigInteger publicKey, BigInteger privateKey, int publicKeyBitCount) throws IllegalArgumentException {
		
		super(publicKeyBitCount, publicKeyBitCount / 8 + 1);
		
		if (publicKeyBitCount % 8 != 0) {
			throw new IllegalArgumentException("The Public Key is not valid (bit count doesn't match)");
		}
		
		mMod = mod;
		mPublicKey = publicKey;
		mPrivateKey = privateKey;
		
		// Debug information.
		Log.d("RSAKeyPair", "N=" + publicKeyBitCount + " - mod = " + mMod + " - PublicKey = " + mPublicKey + " - PrivateKey = " + mPrivateKey);
	}
	
	/**
	 * Encrypt a array of bytes using the RSA Algorithm
	 * @param data data to be encrypted
	 * @return data after encryption
	 */
	public BigInteger encrypt(BigInteger data) throws NullPointerException {
		
		if (mPublicKey == null) {
			Log.e("RSA public key", "The public key is not defined.");
			throw new NullPointerException("The public key is not defined.");
		}
		
		return data.modPow(mPublicKey, mMod);
	}
	
	/**
	 * Encrypt a array of bytes using the RSA Algorithm
	 * @param data Data to be encrypted
	 * @return data after encryption
	 * @throws NullPointerException if the public key is not defined <br><br>
	 * 
	 * <u><b>RSA Encryption/Decryption: </b></u> <br><br>
	 * 
	 * <b>c = m^d [mod n]</b> and <b>m = c^p [mod n]</b> where<br>
	 *   - d = public Key<br>
	 *   - p = private key<br>
	 *   - m = unencrypted Data<br>
	 *   - c = encrypted Data<br>
	 *   - n = modulo<br>
	 */
	public byte[] encrypt(byte[] data) {
		
		// Get the size to be (when encrypted) of each byte.
		int encryptedBlockSize = getEncryptedBlockSize();
		
		// We create a buffer of exactly the right number of crypted byte to be.
		byte[] buffer = new byte[encryptedBlockSize * data.length];
		
		int index = 0;
		for (byte d: data) { // We take each byte of the unencrypted data.
			
			// We encrypt the byte and get the ByteArray.
			byte[] encryptedBytes = encrypt(new BigInteger(Integer.toString(0xFF & d))).toByteArray();
			
			// Translate the index to add some 0 in front of the encryptedBytes if the length is lower than encryptedBlockSize.
			index += encryptedBlockSize - encryptedBytes.length;
			
			// We copy the encrypted byte into the buffer, at the corresponding index.
			System.arraycopy(encryptedBytes, 0, buffer, index, encryptedBytes.length);
			index += encryptedBytes.length;
		}
		
		return buffer;
	}
	
	/**
	 * Decrypt a BigInteger with the RSA Algorithm
	 * @param data Data to be decrypted
	 * @return Data after decryption
	 * @throws NullPointerException if the private key is not defined.
	 */
	public BigInteger decrypt(BigInteger data) throws NullPointerException{
		
		if (mPrivateKey == null) { // If so we cannot decrypt data.
			
			Log.e("RSA private key", "The private key is not defined.");
			throw new NullPointerException("The private key is not defined.");
		}
		
		return data.modPow(mPrivateKey, mMod);
	}
	
	/**
	 * Decrypt a ByteArray with the RSA Algorithm
	 * @param data Data to be decrypted
	 * @return Data after decryption<br><br>
	 * 
	 * <u><b>RSA Encryption/Decryption: </b></u> <br><br>
	 * 
	 * <b>c = m^d [mod n]</b> and <b>m = c^p [mod n]</b> where<br>
	 *   - d = public Key<br>
	 *   - p = private key<br>
	 *   - m = unencrypted Data<br>
	 *   - c = encrypted Data<br>
	 *   - n = modulo<br>
	 */
	public byte[] decrypt(byte[] data) {
		
		int index = 0;
		
		// Number of bytes of encrypted data needed to obtain one byte of unencrypted data.
		int encryptedBlockSize = getEncryptedBlockSize();
		
		byte[] buffer = new byte[data.length/encryptedBlockSize]; // unencrypted data that is going to be returned.
		byte[] tmp;
		
		for (int i=0; i<data.length; i+=encryptedBlockSize) { // For each range of bytes of the encrypted data.
			
			// Take the full range of bytes that represents an crypted block.
			tmp = Arrays.copyOfRange(data, i, i+encryptedBlockSize);
			
			// Decrypt the bytes and put it into the tmp buffer.
			tmp = decrypt(new BigInteger(tmp)).toByteArray();
			
			buffer[index++] = tmp[tmp.length - 1]; // We take only the MSB
		}
		
		return buffer;
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the modulo.
	 * @return modulo.
	 */
	public BigInteger getModulo() {
		return mMod;
	}

	/**
	 * returns the public key.
	 * @return the public key.
	 */
	public BigInteger getPublicKey() {
		return mPublicKey;
	}
}
