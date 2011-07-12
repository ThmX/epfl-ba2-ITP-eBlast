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

import eblast.torrent.Torrent;
import eblast.torrent.peer.PeerHandler;
import eblast.torrent.piece.Block;
import eblast.torrent.piece.Piece;

/**
 * This class is a part of the Visitor Pattern, it allows us
 * to process the message. 
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 26.05.2011 - Initial version
 */
public class MessageHandler implements MessageVisitor {

	private Torrent mTorrent;
	private PeerHandler mPeerHandler;
	
	/**
	 * Default constructor.
	 * @param peerHandler peerHandler is used to get the torrent.
	 */
	public MessageHandler(PeerHandler peerHandler) {
		mPeerHandler = peerHandler;
		mTorrent = mPeerHandler.getTorrent();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(BitField bf) {
		mPeerHandler.setPeerAvailablePieces(bf.getAvailablePiecesIndexes());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(Have h) {
		mPeerHandler.addPeerAvailablePiece(h.getIndex());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Choke c) {
		mPeerHandler.setAmIChoked(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(Unchoke un) {
		mPeerHandler.setAmIChoked(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Interested i) {
		mPeerHandler.setIsHeInterested(true);
		
		mPeerHandler.addMessage(new Unchoke());
		mPeerHandler.setIsHeChoked(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotInterested notI) {
		mPeerHandler.setIsHeInterested(false);
		
		mPeerHandler.addMessage(new Choke());
		mPeerHandler.setIsHeChoked(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(Request req) {
		if (mPeerHandler.isHeChoked() || !mPeerHandler.isHeInterested()) return; // Ignore if he's choked or not interested
		
		int index = req.getIndex();
		int begin = req.getBegin();
		int len = req.getBlockLength();
		Block block;
		try {
			block = mTorrent.getPieces().get(index).getBlockByOffset(begin);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// We send the block only if the size is the same
		if (block.getSize() == len) {
			mPeerHandler.addMessage(new SendBlock(index, begin, block.toBytes()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SendBlock sb) {
		boolean checked = false;
		int index;
		
		try {
			index = sb.getIndex();
			int begin = sb.getBegin();
			Block block = sb.getBlock();
			
			Piece piece = mTorrent.getPieces().get(index);
			
			piece.feed(begin, block); // Feed the block into the piece
			
			checked = piece.check();
			
		} catch (Exception e) {
			return; // An error has occured, then we do nothing
		}
		
		// Send that we have the whole piece
		if (checked) {
			mPeerHandler.addMessage(new Have(index));
		}
	}
	
	// ----- Every SendRSAKey/SendSymmetric Message not treated by Handshake will disconnect us from the peer. ----- 
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(SendRSAKey rsa) {
		
		mPeerHandler.disconnectFromError();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(SendSymmetricKey sym) {
		
		mPeerHandler.disconnectFromError();
	}
	
	// -------------------------------------------------------------------------------------

}
