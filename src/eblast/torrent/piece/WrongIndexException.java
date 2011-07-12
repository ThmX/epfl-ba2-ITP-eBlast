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
 * This Exception is thrown when an index is not correct.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 */
public class WrongIndexException extends EBlastException {
	/**
	 * Auto-generated SerialVersionUID.
	 */
	private static final long serialVersionUID = 4291269839789250394L;

	/**
	 * Thrown when the given index is not correct.
	 * @param required Byte Length that should have been received.
	 * @param received Byte Length that has been received.
	 */
	public WrongIndexException() {
		super("The index should be a multiple of "+Block.BLOCK_SIZE);
	}
}
