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

package eblast.torrent.tracker;

/**
 * This Exception is there to inform the user that a required key is missing.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class TrackerInfoRequiredKeyException extends TrackerInfoException {
	/**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = 8222292740618950335L;
	

	/**
	 * This exception will display a message that the key is missing.
	 * @param m The name of the key
	 */
	public TrackerInfoRequiredKeyException(String keyName) {
		super("The key \"" + keyName + "\" is required but is missing.");
	}
}
