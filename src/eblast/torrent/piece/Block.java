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

/**
 * Represent the piece unit: a block.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 01.03.2011 - Initial version
 */
public class Block implements Cloneable {

	// Constants
	public static final int BLOCK_SIZE = 1 << 14; // 2^16 by default.
	
	private int mSize;
	private byte[] mData;
	
	/**
	 * Default constructor
	 * @param data data contained into this block
	 * @throws BlockLengthException
	 */
	public Block(byte[] data) throws BlockLengthException {
		
		if (data == null) {
			throw new NullPointerException("the given block is null!");
		}	
		else if (data.length>BLOCK_SIZE || data.length==0) { 	// If the data is bigger than the average block or empty, throws an exception.
			throw new BlockLengthException(BLOCK_SIZE, data.length);
		}
			
		this.mData = data;
		this.mSize = data.length; // Note: The data can be shorter than the average block if it's the last block of a piece.	
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * 
	 * @return The size of the current block.
	 */
	public int getSize() {
		return mSize;
	}
	
	/**
	 * Returns the array of bytes.
	 * @return copy of the data.
	 */
	public byte[] toBytes() {
		return mData;
	}
}
