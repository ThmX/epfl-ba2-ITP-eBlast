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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eblast.torrent.piece.Piece;

/**
 * This class represents BitField Message according to the Bittorrent protocol.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class BitField extends Message {

	private static final int EIGHTH_BIT = 0x80;	// MSB of a byte (eighth bit)
	
	private byte[] payload;
	
	/**
	 * Default constructor.
	 * @param payload Additional informations contained with the message.
	 */
	public BitField(byte[] payload) {
		
		super(DEFAULT_LENGTH + payload.length, ID.bitfield);
		this.payload = payload;
	}
	
	/**
	 * Create a BitField message
	 * @param pieces
	 */
	public BitField(List<Piece> pieces) {
		super(DEFAULT_LENGTH + (int)Math.ceil(pieces.size() / 8) , ID.bitfield);
		
		payload = new byte[getLength() - DEFAULT_LENGTH];
		
		int index = 0;
		int bitWise = EIGHTH_BIT; // Start from the MSB
		for (Piece p: pieces) {
			
			if (p.isComplete()) {
				payload[index] |= bitWise; // put the n-th bit to 1
			}
			
			bitWise >>= 1; // Go to the next bit (on the right)
			
			// If we've done all bits from a byte, we take the next byte and re-init the bitWise.
			if (bitWise == 0x00) {
				bitWise = EIGHTH_BIT;
				index++;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write(MessageOutputStream mos) throws IOException {
		super.write(mos);
		
		for (byte b: payload) {
			mos.writeByte(b);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}
	
	/**
	 * This methods gives us the indexes of the pieces that is contained into the current BitField payload.
	 * @return indexes of the pieces contained into the payload.
	 */
	public Set<Integer> getAvailablePiecesIndexes() {
		Set<Integer> piecesIndexes = new HashSet<Integer>();
		int index = 0;
		for (int i = 0; i < payload.length; i++) {
			byte b = payload[i];
			for (int j = 0; j < 8; j++) {
				if ((b & EIGHTH_BIT) == EIGHTH_BIT) { // Is the MSB==1 ?
					piecesIndexes.add(index);
				}
				b <<= 1; // Take the next bit (on the right)
				index++;
			}			
		}
		
		return piecesIndexes;
	}
	
}
