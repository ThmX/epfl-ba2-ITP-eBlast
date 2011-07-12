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

package eblast.io;

/**
 * This class is there to contain a pair of (download, upload) speed values.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @verison 1.0 - 18.05.2011 - Initial version
 */
public class SpeedPair {

	public double download;
	public double upload;
	
	/**
	 * Default constructor.
	 * @param down Download in byte per second
	 * @param up Upload in byte per second
	 */
	public SpeedPair(double down, double up) {
		download = down;
		upload = up;
	}
	
}
