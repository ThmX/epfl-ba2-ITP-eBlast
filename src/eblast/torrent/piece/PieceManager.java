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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import eblast.log.Log;
import eblast.torrent.Torrent;
import eblast.torrent.Torrent.TorrentStates;
import eblast.torrent.messages.Request;
import eblast.torrent.peer.PeerHandler;

/**
 * This class will allow us to manage the List of Pieces contained in a torrent object (given in parameters).
 * In this class we will only use indexes to reference Pieces.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.03.2011 - Initial version
 */
public class PieceManager {

	private static final int MAX_REQUESTS = 100;
	
	private Torrent			mTorrent;
	private List<Piece>		mPiecesOfInterest;		// Pieces that we would like to have.
	private Stack<Piece>	mNeededPieces;			// All the pieces of the torrent that we currently need.
	
	/**
	 * Main constructor.
	 * @param torrent Instance of the current torrent.
	 */
	public PieceManager(Torrent torrent) {
		mTorrent = torrent;

		mPiecesOfInterest = Collections.synchronizedList(new LinkedList<Piece>());
		mNeededPieces = new Stack<Piece>(); // No need to be synchronized
		
		// We add all incomplete piece into the NeededPieces Stack
		for (Piece p: mTorrent.getPieces()) {
			if (!p.isComplete()) {
				mNeededPieces.push(p);
			}
		}
		// And finally, shuffle it
		Collections.shuffle(mNeededPieces);
	}
	
	/**
	 * Main threaded method of this class.
	 */
	public void launch() {
		new Thread("PieceManager(" + mTorrent + ")") {
			public void run() {
				while (mTorrent.getTorrentState() != TorrentStates.completed) { // While the torrent is not finished...
					updatePriorities(); // we update the priorities
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException e) {
						Log.e("PieceManager", "Thread.sleep()"); // Log an error
					}
				}
			}
		}.start();
	}

	/**
	 * Updates the pieces that we would like to have in our torrent.
	 */
	public void updatePriorities() {

		synchronized (mPiecesOfInterest) {
			// Can't use foreach because we need to be able to remove a piece from the list.
			for (int i=0; i<mPiecesOfInterest.size();) {
				Piece p = mPiecesOfInterest.get(i);
				if (p.isComplete()) {
					// Remove the piece from PiecesOfInterest
					mPiecesOfInterest.remove(p);
						
				} else {
					i++;
				}
			}
			// We take as many as needed pieces we need BUT at most MAX_REQUESTS and put it into a list of Interest
			for (int i=mPiecesOfInterest.size(); (i < MAX_REQUESTS) && (mNeededPieces.size() != 0); i++) {
				
				// Transfer one Piece from NeededPieces to PiecesOfInterest.
				mPiecesOfInterest.add(mNeededPieces.pop());
			}
		}

	}
	
	/**
	 * Returns a piece that we want and that is available from the peer if it exists. Otherwise returns null.
	 * @param piecesAvailable Indexes of all the pieces available from the peer.
	 * @return piece that we would like to have and that is available from the peer.
	 */
	public Piece getNeededAndAvailablePiece(Set<Integer> piecesAvailable) {
		
		synchronized (mPiecesOfInterest) {
			// We sort the list to have in front the most interesting piece
			if (mPiecesOfInterest.size() > 0) {
				try {
					Collections.sort(mPiecesOfInterest);
				} catch (NoSuchElementException e) {
					System.out.println(mPiecesOfInterest);
				}
			}
			
			// From the most to the least interested
			for (Piece p: mPiecesOfInterest) {
				// If the peer contains the piece, we request this one
				if (piecesAvailable.contains(p.getIndex())) {
					return p;
				}
			}
		}
			
		return null; // If the peer has no piece we need.
	}
	
	public void addRequest(Request request, PeerHandler peerHandler) {
		mTorrent.getPieces().get(request.getIndex()).addPairRequestForBlock(request, peerHandler);
	}
	
	public void removeRequest(Request request) {
		mTorrent.getPieces().get(request.getIndex()).removePairRequestPeerHandlerForBlock(request);
	}
	
	/**
	 * @return A set containing all piece that we currently requested.
	 */
	public Set<Integer> getRequestedPieces() {
		Set<Integer> requestedPieces = new HashSet<Integer>();
		for (Piece p: mPiecesOfInterest) {
			requestedPieces.add(p.getIndex());
		}
		return requestedPieces;
	}
}
