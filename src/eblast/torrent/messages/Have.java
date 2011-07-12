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

/**
 * This class represents Have Message according to the Bittorrent protocol.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class Have extends Message {
	
	public static final int DEFAULT_LENGTH = 5;
	
	private int mIndex;
	
	/**
	 * Default constructor.
	 */
	public Have(int index) {
		
		super(DEFAULT_LENGTH, ID.have);
		mIndex = index;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		
		mos.writeInt(mIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}

	/************************ GETTERS / SETTERS *************************/
	
	public int getIndex() {
		return mIndex;
	}
}