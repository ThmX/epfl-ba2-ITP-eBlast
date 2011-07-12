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

import eblast.torrent.piece.Block;
import eblast.torrent.piece.BlockLengthException;

/**
 * This class represents Piece(SendBlock) Message according to the Bittorrent protocol.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version
 */
public class SendBlock extends Message {
	
	public static final int DEFAULT_LENGTH = 9;

	private int plIndex;
	private int plBegin;
	private byte[] plBlock;
	
	/**
	 * Default constructor.
	 * @param payloadIndex integer specifying the zero-based piece index.
	 * @param payloadBegin integer specifying the zero-based byte offset within the piece.
	 * @param payLoadBlock  block of data, which is a subset of the piece specified by index.
	 */
	public SendBlock(int payloadIndex, int payloadBegin, byte[] payLoadBlock) {
		
		super(DEFAULT_LENGTH + payLoadBlock.length, ID.piece);
		
		plIndex = payloadIndex;
		plBegin = payloadBegin;
		plBlock = payLoadBlock;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		mos.writeInt(plIndex);
		mos.writeInt(plBegin);
		mos.write(plBlock);
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
	 * Returns the Block that we have to send
	 * @return Block that we have to send
	 */
	public Block getBlock() throws BlockLengthException {
		return new Block(plBlock);
	}
}
