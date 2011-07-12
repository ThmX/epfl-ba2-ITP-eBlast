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
 * Interface to construct a Counter Stream.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 18.05.2011 - Initial version
 */
public interface ICounterStream {
	
	/**
	 * Reinitializes the counter.
	 */
	public void resetCounter();
	
	/**
	 * @return total number of bytes that has been transferred
	 */
	public long getTotalBytesRead();
	
	/**
	 * @return speed in bytes per seconds
	 */
	public double getAverageSpeed();
}
