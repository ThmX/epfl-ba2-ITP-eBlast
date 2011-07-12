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

package eblast.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import eblast.crypto.KeyPair;

/**
 * InputStream that will allow us to fetch encrypted data from an InputStream and decrypt it on the fly.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 19.05.2011 - Initial version
 */
public class CryptoInputStream extends FilterInputStream {

	private KeyPair mKeygen;
	private int mBlockSize;
	
	/**
	 * Main constructor.
	 * @param in InputStream that contains the encrypted data.
	 * @param keyPair KeyPair that contains all the informations to encrypt/decrypt data using a specific algorithm.
	 */
	public CryptoInputStream(InputStream in, KeyPair keyPair) {
		super(in);
		mKeygen = keyPair;
		mBlockSize = keyPair.getEncryptedBlockSize();
	}
	
	/**
	 * Read flow from the InputStream, decrypt it and store it into b.
	 * @return The byte read, -1 otherwise
	 */
	public int read() throws IOException {
		byte[] buf = new byte[1];
		return (read(buf, 0, 1) > 0) ? 0xFF & buf[0] : -1;
	}
	
	/**
	 * Read flow from the InputStream, decrypt it and store it into b.
	 * @param b byte array that is used to store the uncrypted flow read on the InputStream.
	 * @return number of bytes read.
	 */
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * Reads up to len bytes of encrypted data from the input stream, decrypt it and store it into an array of bytes.
	 * @param b the buffer into which the data is read.
	 * @param off the start offset in array b at which the data is written.
	 * @param len the maximum number of bytes to read.
	 * @return number of decrypted bytes read.
	 */
	public int read(byte[] buf, int off, int len) throws IOException {
		
		len *= mBlockSize;	// If we want to read len byte, we'll have to read len*mBlockSize
		
		byte[] buffer = new byte[len];
		int newLen = 0;
		do {
			int tmp = in.read(buffer, 0, len - newLen); // Read all bytes, but get the number we read.
			if ((tmp < 0) && (newLen == 0)) { // If we have a negative number of read byte and it's the first run, we quit.
				return tmp;
			}
			newLen += (tmp > 0) ? tmp : 0;
		} while ((newLen % mBlockSize) > 0); // Ensure to always have a mBlockSize number of byte to decryt.

		if (newLen < 0) { // if there isn't any byte to decrypt
			return newLen;
		}
		
		buffer = mKeygen.decrypt(Arrays.copyOfRange(buffer, 0, newLen));
		System.arraycopy(buffer, 0, buf, off, buffer.length);
		
		return buffer.length;
	}

	/**
	 * Returns an estimate of the number of decrypted bytes that can be read (or skipped over).
	 * @return estimate of the number of decrypted bytes that can be read or skipped over.
	 */
	public int available() throws IOException {
		return in.available() / mBlockSize;
	}

	/**
	 * Closes this input stream and releases any system resources associated with the stream.
	 */
	public void close() throws IOException {
		super.close();
	}
}
