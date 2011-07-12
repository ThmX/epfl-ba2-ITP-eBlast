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
import java.util.Date;

/**
 * This class represents a Request Message according to the Bittorrent protocol.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class Request extends Message {
	
	private static final long FIVE_SECONDS = 5000L;
	
	public static final int DEFAULT_LENGTH = 13;

	/**
	 * payload arguments.
	 */
	private int plIndex;
	private int plBegin;
	private int plLength;
	
	long mCreated = new Date().getTime();
	
	/**
	 * Default constructor.
	 * @param index integer specifying the zero-based piece index.
	 * @param begin integer specifying the zero-based byte offset within the piece.
	 * @param length integer specifying the requested length.
	 */
	public Request(int index, int begin, int length) {
		super(DEFAULT_LENGTH, ID.request);
		
		plIndex = index;
		plBegin = begin;
		plLength = length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		mos.writeInt(plIndex);
		mos.writeInt(plBegin);
		mos.writeInt(plLength);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the index payload argument
	 * @return index payload argument
	 */
	public int getIndex() {
		return plIndex;
	}
	
	/**
	 * Returns the begin payload argument
	 * @return begin payload argument
	 */
	public int getBegin() {
		return plBegin;
	}
	
	/**
	 * Returns the block length payload argument
	 * @return begin block length argument
	 */
	public int getBlockLength() {
		return plLength;
	}
	
	/**
	 * Returns true if the request is "older" than 5 seconds, false otherwise
	 * @return true if the request is "older" than 5 seconds, false otherwise
	 */
	public boolean isOld() {
		return (mCreated - new Date().getTime()) >= FIVE_SECONDS; // The request is 5 seconds older
	}
}
