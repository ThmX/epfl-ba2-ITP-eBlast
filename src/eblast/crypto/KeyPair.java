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

/**
 * This interface contains the necessary methods to encrypt and decrypt
 * a given message using various algorithms.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0   - 22.02.2011 - Initial version
 * @version 1.0.1 - 28.02.2011 - Change name from KeyPair to IKeyPair
 * @version 1.0.2 - 28.02.2011 - It became an abstract class, so the name is again Keypair.
 */
public abstract class KeyPair {
	
	private int mPublicKeyBitsCount;
	private int mEncryptedBlockSize;
	
	/**
	 * Default constructor.
	 * @param publicKeyBitsCount number of bits into the public key.
	 * @param encryptedBlockSize number of bytes of an encrypted block.
	 */
	public KeyPair(int publicKeyBitsCount, int encryptedBlockSize) {
		
		mPublicKeyBitsCount = publicKeyBitsCount;
		mEncryptedBlockSize = encryptedBlockSize;
	}
	
	/**
	 * Encrypt data using the inherited algorithm
	 * @param data array of bytes to be encrypted
	 * @return data after encryption
	 */
	abstract public byte[] encrypt(byte[] data);

	/**
	 * Encrypt data using the inherited algorithm
	 * @param data BigInteger to be encrypted
	 * @return data after encryption
	 */
	abstract public BigInteger encrypt(BigInteger data);
	
	/**
	 * Decrypt using the inherited algorithm
	 * @param data Data to be decrypted
	 * @return Data after decryption
	 */
	abstract public byte[] decrypt(byte[] data);
	
	/**
	 * Decrypt using the inherited algorithm
	 * @param data BigInteger to be decrypted
	 * @return data after decryption
	 */
	abstract public BigInteger decrypt(BigInteger data);
	
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * returns the number of bits into the public key.
	 * @return number of bits into the public key.
	 */
	public int getPublicKeyBitCount() {
		return mPublicKeyBitsCount;
	}
	
	/**
	 * returns the number of bytes of an encrypted block.
	 * @return number of bytes of an encrypted block.
	 */
	public int getEncryptedBlockSize() {
		return mEncryptedBlockSize;
	}
}
