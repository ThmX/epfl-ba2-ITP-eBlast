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

package eblast.crypto;

import eblast.log.Log;

/**
 * This Exception Class informs the user that the entered key has a wrong length.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class KeyLengthException extends Exception {
	/**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = 4069724293650142925L;

	/**
	 * Default constructor.
	 * @param m message to be displayed.
	 */
	public KeyLengthException(String m) {
		super(m);
		Log.e("Key Length exception", m);
	}
}
