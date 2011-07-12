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

package eblast.torrent.piece;

import eblast.exceptions.EBlastException;

/**
 * This Exception is thrown when a Piece has a wrong length.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 */
public class PieceLengthException extends EBlastException {
	/**
	 * Auto-generated SerialVersionUID.
	 */
	private static final long serialVersionUID = 4291269839789250394L;

	/**
	 * Thrown when a Piece has the wrong length.
	 * @param required Byte Length that should have been received.
	 * @param received Byte Length that has been received.
	 */
	public PieceLengthException(int received) {
		super("Piece length should be between 32KBits and 4Mbits, yours is " + received + ".");
	}
}
