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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import eblast.log.Log;

/**
 * This wrapper Class allows us to write messages (including Handshake)
 * from a generic OutputStream.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class MessageOutputStream extends DataOutputStream {
	public MessageOutputStream(OutputStream os) {
		super(os);
	}
	
	/**
	 * Writes the given message into this Stream
	 * @param msg message to write into this Stream
	 * @throws IOException
	 */
	public void write(Message msg) throws IOException {
		Log.d("MessageOutputStream", "message("+ msg.getId().name() + ") has been written."); // Debug
		msg.write(this);
		flush();
	}
	
	/**
	 * Sends the current Handshake into the peer's MessageOutputStream.
	 */
	public void write(Handshake handshake) throws IOException {
		try {
			writeByte(Handshake.DEFAULT_PSTR.length());
			writeBytes(Handshake.DEFAULT_PSTR);
			write(handshake.getReserved());
			write(handshake.getInfoHash().toBytes());
			writeBytes(handshake.getPeerId());
			
		} catch (IOException e) {
			Log.e("Handshake", "Error while sending the Handshake");
			throw e;
		}
	}
}
