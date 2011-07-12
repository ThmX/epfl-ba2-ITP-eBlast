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

/**
 * Abstract class that represents a basic Message (according to the Bitorrent protocol) of the following form:
 * <length><id><payload>
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 12.04.2011 - Initial version
 */
public abstract class Message {

	public static final byte DEFAULT_LENGTH = 1;
	
	/**
	 * Different messages used by the Bittorent protocol.
	 *
	 */
	public static enum ID {
	      choke,
	      unchoke,
	      interested,
	      notInterested,
	      have,
	      bitfield,
	      request,
	      piece,
	      cancel,
	      port,
	      sendRSAKey,
	      sendSymmetricKey
		};
	
	private int mLength;
	private ID mID;
		
	/**
	 * Default constructor.
	 * @param length length of the Message (id and payload).
	 * @param id ID of the message.
	 */
	public Message(int length, ID id) {
		
		this.mLength = length;
		this.mID = id;
	}

	/**
	 * Writes the Bitfield message into the OutputStream given in parameter.
	 * @param mos OutputStream in which we are going to write the Bitfield message
	 * @exception IOException
	 */
	public void write(MessageOutputStream mos) throws IOException {
		mos.writeInt(mLength);
		mos.writeByte((byte)mID.ordinal());
	}
	
	/**
	 * Used by the Visitor design Pattern.
	 */
	abstract public void accept(MessageVisitor v);
	
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the length of this message
	 * @return length of this message
	 */
	public int getLength() {
		return mLength;
	}
	
	/**
	 * Returns the ID of this message
	 * @return ID of this message
	 */
	public ID getId() {
		return mID;
	}
}
