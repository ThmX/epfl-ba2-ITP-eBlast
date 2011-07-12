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

package eblast.gui;

import eblast.torrent.Torrent;

/**
 * This interface should be implemented if your class
 * has to be notified when there is a selection change
 * into the Torrent List.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public interface UpdateSelection {

	/**
	 * This method is called each time there is a selection change in
	 * the Torrent List.
	 * @param torrentSelection the changed selection
	 */
	public void update(Torrent[] torrentSelection);
}
