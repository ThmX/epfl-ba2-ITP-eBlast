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

import eblast.log.Log;

/**
 * Message to send when we want to transmit
 * the symmetric key, which is necessary to
 * decrypt data.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class SendSymmetricKey extends Message {
	
	private byte[] mEncryptionKey;
	
	/**
	 * Default constructor
	 * @param encryptionKey the encryption key to use
	 */
	public SendSymmetricKey(byte[] encryptionKey) {
		
		super(DEFAULT_LENGTH + encryptionKey.length, ID.sendSymmetricKey);
		
		Log.d("SendSymmetricKey", "len = " + encryptionKey.length); // Debug
		mEncryptionKey = encryptionKey;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		mos.write(mEncryptionKey);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the symmetric key
	 * @return the symmetric key
	 */
	public byte[] getKey() {
		return mEncryptionKey;
	}
}
