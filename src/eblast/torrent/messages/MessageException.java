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

import eblast.exceptions.EBlastException;

/**
 * When an Exception about a message is thrown.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 26.05.2011 - Initial version
 */
public class MessageException extends EBlastException {
	/**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = 5489895255750692356L;
	
	/**
	 * An exception has occurred when trying to create a specifiac Message.
	 * @param id ID of message
	 * @param msg Message to transmit
	 */
	public MessageException(Message.ID id, String msg) {
		super("Message (ID=" + id.toString() + ") -> " + msg);
	}
}
