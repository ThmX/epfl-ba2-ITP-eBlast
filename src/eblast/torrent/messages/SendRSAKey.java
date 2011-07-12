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

import java.io.IOException;
import java.math.BigInteger;

/**
 * Message to send when we want to transmit
 * all the necessary material to encrypt a message,
 * which is public key, length of the modulo and the modulo itself.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class SendRSAKey extends Message {

	public static final int DEFAULT_LENGTH = 13; // Without the key length and modulo length.
	
	private int mModuloBitCount;
	
	private byte[] mKey;
	private byte[] mModulo;
	
	/**
	 * Default constructor
	 * @param moduloBitCount Length of the modulo in bits
	 * @param key public key
	 * @param modulo modulo
	 */
	public SendRSAKey(int moduloBitCount, BigInteger key, BigInteger modulo) {
		
		super(DEFAULT_LENGTH + key.toByteArray().length + modulo.toByteArray().length, ID.sendRSAKey);
		
		mModuloBitCount = moduloBitCount;
		
		mKey = key.toByteArray();
		mModulo = modulo.toByteArray();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		mos.writeInt(mModuloBitCount);
		mos.writeInt(mKey.length);
		mos.write(mKey);
		mos.writeInt(mModulo.length);
		mos.write(mModulo);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the public key
	 * @return public key
	 */
	public BigInteger getKey() {
		return new BigInteger(mKey);
	}

	/**
	 * Returns the modulo
	 * @return the modulo
	 */
	public BigInteger getModulo() {
		return new BigInteger(mModulo);
	}

	/**
	 * Returns the length of the modulo in bits
	 * @return length of the modulo in bits
	 */
	public int getModuloBitCount() {
		return mModuloBitCount;
	}
}
