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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import eblast.crypto.KeyPair;

/**
 * OutputStream that will allow us to encrypt data and write it on an OutputStream on the fly.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 19.05.2011 - Initial version
 */
public class CryptoOutputStream extends OutputStream {

	private KeyPair mKeygen;
	private OutputStream out;
	
	/**
	 * Main constructor.
	 * @param out OutputStream where the data is going to be written.
	 * @param keygen IKeyPair that contains all the informations to encrypt/decrypt data using a specific algorithm.
	 */
	public CryptoOutputStream(OutputStream out, KeyPair keygen) {
		this.out = out;
		mKeygen = keygen;
	}

	/**
	 * Encrypt and write the specified integer to this output stream
	 * @param b byte array to be written
	 */
	public void write(int b) throws IOException {
		write(new byte[]{(byte)b}, 0, 1);
	}

	/**
	 * Encrypt and write the specified byte array to this output stream.
	 * @param b byte array to be written.
	 */
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	/**
	 * Writes len bytes from the specified byte array starting at offset off to this output stream.
	 * @param b the data.
	 * @param off the start offset in the data.
	 * @param len the number of bytes to write.
	 */
	public void write(byte[] b, int offset, int len) throws IOException {
		
		// Encrypt data, then write it on the OutputStream.
		byte[] encB = mKeygen.encrypt(Arrays.copyOfRange(b, offset, offset+len));
		out.write(encB, 0, encB.length);
	}
	
	/**
	 * Closes this output stream and releases any system resources associated with this stream.
	 * @throws IOException
	 */
	public void close() throws IOException {
		out.close();
	}
	
	/**
	 * Flushes this output stream and forces any buffered output bytes to be written out.
	 * @throws IOException
	 */
	public void flush() throws IOException {
		out.flush();
	}
}
