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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import eblast.checksum.Hash;
import eblast.log.Log;

/**
 * This wrapper Class allows us to read messages (including Handshake)
 * from a generic InputStream.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class MessageInputStream extends DataInputStream {

	/**
	 * Default constructor
	 * @param is InputStream where the data are to be read
	 */
	public MessageInputStream(InputStream is) {
		super(is);
	}
	
	/**
	 * This wrapping method returns a Message stored into this Stream
	 * @return Message stored into this Stream
	 * @throws IOException
	 * @throws MessageException
	 */
	public Message readMessage() throws IOException, MessageException {
		return FactoryMessage.createMessage(this);
	}
	
	/**
	 * Read a Handshake Message from this Stream
	 * @return Handshake read from this Stream
	 */
	public Handshake readHandShake() throws IOException {
		try {
			byte[] buffer;
			
			// Content of the temporary object that is going to be returned.
			int pstrlen = readByte();
			
			if (pstrlen != Handshake.DEFAULT_PSTR.length()) throw new IOException("Wrong handshake message length");
			
			buffer = new byte[pstrlen];
			readFully(buffer);
			String tmpPstr 		= new String(buffer, "ASCII");
			
			// If it is not a HandShake message or if is the handshake message is wrong, we quit.
			if (!Handshake.DEFAULT_PSTR.equalsIgnoreCase(tmpPstr)) throw new IOException("Wrong Handshake");
			
			// pstr is valid then we continue
			
			byte[] tmpReserved 	= new byte[Handshake.RESERVED_LENGTH];			
			readFully(tmpReserved);

			byte[] tmpInfoHash	= new byte[Handshake.INFO_HASH_LENGTH];
			readFully(tmpInfoHash);
			Hash infoHash = new Hash(tmpInfoHash);

			byte[] tmpPeerId	= new byte[Handshake.PEER_ID_LENGTH];
			readFully(tmpPeerId);
			
			Handshake tmpHandShake = new Handshake(infoHash, new String(tmpPeerId), tmpReserved);
			
			return tmpHandShake;
			
		} catch (IOException e) {
			Log.e("Handshake", "Problem while reading the Handshake data from a buffer.");
			throw e;
		}
	}
}
