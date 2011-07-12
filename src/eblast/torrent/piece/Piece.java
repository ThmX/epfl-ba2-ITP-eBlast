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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Semaphore;

import eblast.checksum.Checksum;
import eblast.checksum.Hash;
import eblast.checksum.NullHashException;
import eblast.io.FileManager;
import eblast.log.Log;
import eblast.torrent.messages.Request;
import eblast.torrent.peer.PeerHandler;

/**
 * This class modelizes a piece of a torrent file.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.02.2011 - Initial version
 * @version 1.1 - 28.02.2011 - Using {@link Hash} Class instead of byte[]
 * and {@link BlockLengthException} instead of {@link ArrayIndexOutOfBoundsException}
 * @version 1.2 - 01.03.2011 - Major change in architecture. After the creation of the Block class,
 * this class has been totally revisited in order to welcome this new class. We also used a regular array
 * for the Block list contained in every piece.
 * @version 1.2 - 06.03.2011 Pass the new Junit test.
 */
public class Piece implements Cloneable, Comparable<Piece> {
	
	private FileManager mFileManager;									// Object used to write blocks into a file
	private int mNbBlocks;												// Number of blocks contained into this piece
	private Set<Integer> mReceivedBlockIndexes;							// All the block (indexes) that we have already
	private boolean mComplete;											// True if the piece is complete, false otherwise
	private int mSize;													// Size of this piece
	private Hash mSignatureSHA1;										// SHA-1 signature of the current piece
	private int mIndex;													// Index of this piece into the torrent
	private List<Map<Request, PeerHandler>> mRequestsPerBlock; 			// Gives us informations about who wants a particular block.
	private Semaphore mRequestsPerBlockSemaphore = new Semaphore(1); 	// To ensure we are writing at the same time in the List of Maps.
	
	/**
	 * Default constructor.
	 * @param mIndex Piece index
	 * @param mSize Actual size of the piece in number of byte. It can be of any size because of the last block.
	 * @param mSignatureSHA1 SHA-1 signature of the piece.
	 */
	public Piece(FileManager fileManager, int index, int size, Hash signature) throws PieceLengthException {
		
		// The size is minimum 32kbits and max 4mbits
		if (size > 32*Block.BLOCK_SIZE) throw new PieceLengthException(size);
		
		mFileManager = fileManager;
		mIndex = index;
		mSize = size;
		mSignatureSHA1 = signature;

		// Allows one more block if the size of the piece
		// is not a multiple of a block, which means that
		// The last block is shorter than the others.
		mNbBlocks = (int) Math.ceil((double)size/Block.BLOCK_SIZE);
		
		// size/Block_size: number of blocks without the last block.
		// The +1 is for the last block, which can be of any size.
		
		mReceivedBlockIndexes = new HashSet<Integer>(mNbBlocks);
		
		// Creates a pair (request, PeerHandler) for each block.
		mRequestsPerBlock = Collections.synchronizedList(new ArrayList<Map<Request,PeerHandler>>(mNbBlocks));
		for (int i=0; i<mNbBlocks; i++) {
			// Synchronized
			mRequestsPerBlock.add( Collections.synchronizedMap(new HashMap<Request, PeerHandler>()) );
		}
	}
	
	/**
	 * Fill the current piece with the given block at the begin position.
	 * @param begin beginning index
	 * @param block blocks we want to copy into the piece
	 * @throws NullHashException
	 * @throws IOException 
	 * @throws NoSuchElementException 
	 */
	synchronized public void feed(int begin, Block block) throws BlockLengthException, WrongIndexException, NullHashException, NoSuchElementException, IOException{
		
		if (begin%Block.BLOCK_SIZE != 0) throw new WrongIndexException(); // If begin is not a correct block address, throws a WrongIndexException.
		
		// if it's not the last block of the piece and if the block is shorter than the other blocks,it throws an BlockLengthException.
		if (mSize-Block.BLOCK_SIZE!=begin && block.getSize()<Block.BLOCK_SIZE && !isLastBlock(begin))
			throw new BlockLengthException(Block.BLOCK_SIZE, block.getSize());
		
		int blockIndex = byte2IndexAddress(begin);
		if (mReceivedBlockIndexes.add(blockIndex)) { // If the block has not been received yet, add it.
			
			if (mFileManager != null) {
				mFileManager.write(this, blockIndex, block); // Writes the received block into the file
			}
			
			// Cancels all the requests for this block.
			cancelPendingRequestFor(blockIndex);
		}
	}
	
	/**
	 * Cancel all the requests that have been made by other PeerHandlers for this Block when it has been received correctly.
	 * @param index Index of the block into the piece (which is the same as in the requestsPerBlock arrayList)
	 */
	private void cancelPendingRequestFor(int index) {
		
		try {
			mRequestsPerBlockSemaphore.acquire();
		} catch (InterruptedException e) {}
		
		Map<Request, PeerHandler> requestPeerHandler = mRequestsPerBlock.get(index);
		for (Entry<Request, PeerHandler> entry: requestPeerHandler.entrySet()) {
			// Deletes the request from the PeerHandler.
			entry.getValue().removeRequest(entry.getKey());
		}
		requestPeerHandler.clear(); // Delete the whole HashMap of requests for the chosen block.			
		
		mRequestsPerBlockSemaphore.release();
	}
	
	/**
	 * Check if the given Block is the last one in the Piece.
	 * @param begin Address where the block starts.
	 * @return true if the block is the last one in the Piece, false otherwise.
	 */
	private boolean isLastBlock(int begin) {
		return begin + Block.BLOCK_SIZE > mSize;
	}
	
	/**
	 * Converts an offset (relative location into the piece, from 0 to piece.size()) to an index
	 * referencing the blocks (block 1,2 to piece.size/BLOCK_SIZE).
	 * @param offset the given offset
	 * @return an index that references a block.
	 */
	private int byte2IndexAddress(int offset) throws WrongIndexException, IndexOutOfBoundsException{
		
		// If its not a good index
		if(offset%Block.BLOCK_SIZE != 0) throw new WrongIndexException();
		
		// Or if it's below zero or above the size of the piece, throws an IndexOutOfBoundsException
		if(offset<0 || offset>mSize) throw new IndexOutOfBoundsException();
		
		return offset/Block.BLOCK_SIZE;
	}
	
	/**
	 * Initialize the piece by reading all blocks and check if the Hashes match.
	 * @return true if the file has been transferred correctly. if false, it will erase 
	 */
	public synchronized boolean init() throws NoSuchAlgorithmException, NullHashException, UnsupportedEncodingException {

		if (mFileManager == null) return false;

		// Get the SHA-1 object instance in order to generate a hash (checksum variable) of the received file.
		Checksum SHA1Checksum = Checksum.getSHA1Instance();
		//SHA1Checksum.reset();
		
		for (int i=0; i<mNbBlocks; i++) { // Adds every block of the piece to the SHA1Checksum.
			Block block;
			try {
				block = mFileManager.read(this, i);
			} catch (NullHashException e) {
				return false;
			} catch (BlockLengthException e) {
				return false;
			} catch (WrongIndexException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			SHA1Checksum.append(block.toBytes());
		}
		
		Hash hash = SHA1Checksum.digest(); // Computes the hash.
		
		// Compares the signature contained into the file and the signature computed with the received data.
		if (mSignatureSHA1.equals(hash)) {
			for (int i=0; i<mNbBlocks; i++) {
				mReceivedBlockIndexes.add(i);
			}
			mComplete = true;
			return true;
		} else {
			resetPiece(); // If not, erase the piece.
			return false;
		}
	}
	
	/**
	 * Check if we received the Piece right.
	 * @return true if the file has been transferred correctly. if false, it will erase 
	 */
	public synchronized boolean check() throws NoSuchAlgorithmException, NullHashException, UnsupportedEncodingException {
		
		return isComplete() ? true : ( (mReceivedBlockIndexes.size() == mNbBlocks) ? init() : false );
	}
	
	/**
	 * Reset the piece data and the index block array.
	 */
	private void resetPiece() {
		Log.e("Piece", "Reset piece " + mIndex);
		mReceivedBlockIndexes.clear();
		mComplete = false;
	}
	
	/**
	 * Return a percentage of completeness of the current piece.
	 * @return Percentage of received blocks.
	 */
	public double getDownloadCompleteness() {				
		return 100.0 * mReceivedBlockIndexes.size() / mNbBlocks;
	}
	
	/**
	 * Fetch a block according to an offset given in parameter.
	 * @param offset Relative offset of the requested block
	 * @return A block object if it exists or throws an exception
	 */
	public Block getBlockByOffset(int offset) throws NoSuchElementException, NullHashException, BlockLengthException, WrongIndexException, IOException {
		
		int index=0;
		try {
			index = byte2IndexAddress(offset); // Transform an offset to an index.
		} catch (WrongIndexException e) {}
		catch (IndexOutOfBoundsException e) {}
		
		return getBlock(index);
	}
	
	/**
	 * Adds a (Request, PeerHandler) pair for the block of index blockIndex given in parameter.
	 * @param blockIndex index of the block.
	 * @param request request to add the the HashMap (key)
	 * @param peerHandler PeerHandler to add the the haskMap (value).
	 */
	public void addPairRequestForBlock(Request request, PeerHandler peerHandler) {
		try {
			mRequestsPerBlockSemaphore.acquire();
		} catch (InterruptedException e) {}

		try {
			
			mRequestsPerBlock.get(byte2IndexAddress(request.getBegin())).put(request, peerHandler);
			
		} catch (WrongIndexException e) {}
		catch (IndexOutOfBoundsException e) {
			Log.e("Piece::IndexOutOfBoundsException", "Index out of bounds exception!");
		}

		mRequestsPerBlockSemaphore.release();
	}

	/**
	 * Removes the pair (Request, PeerHandler) for the block of index <blockIndex> according to the key <key> , both given in parameter.
	 * @param blockIndex index of the block.
	 * @param request key for which we will delete the associated entry into the Hashmap.
	 */
	public void removePairRequestPeerHandlerForBlock(Request request) {
		try {
			mRequestsPerBlock.get(byte2IndexAddress(request.getBegin())).remove(request);
			
		} catch (WrongIndexException e) {}
		catch (IndexOutOfBoundsException e) {
			Log.e("Piece::IndexOutOfBoundsException", "IndexOutOfBoundsException");
		}
	}
	
	/**
	 * Gives the average number of requests that the current Piece have on it.
	 * @return average number of requests that the current Piece have on it.
	 */
	public double getAverageNumberOfRequestPairs() {
		
		try {
			mRequestsPerBlockSemaphore.acquire();
		} catch (InterruptedException e) {}
		
		int numberOfPairs = 0;
		
		for (Map<Request, PeerHandler> m : mRequestsPerBlock) { // For each Map of every Block.
			numberOfPairs += m.size(); // we sum the size of the Hashmaps of every Blocks.
		}
		
		mRequestsPerBlockSemaphore.release();
		
		return (double)numberOfPairs / mRequestsPerBlock.size(); // Returns the average number of requests made on this piece.
	}
	
	/**
	 * Returns the least requested Block of the Piece.
	 * @return the least requested Block of the Piece, null if none
	 */
	public int getLeastRequestedBlockIndex() {
		
		try {
			mRequestsPerBlockSemaphore.acquire();
		} catch (InterruptedException e) {}
		
		int leastRequestedBlockIndex = -1;
		int leastNumberOfRequests = Integer.MAX_VALUE;
		int currentNumberOfRequests;
		
		for (int i=0; i<mNbBlocks; i++) { // For each block in the piece
			if (!mReceivedBlockIndexes.contains(i)) {
				currentNumberOfRequests = mRequestsPerBlock.get(i).size();
				if (currentNumberOfRequests < leastNumberOfRequests) {
					leastRequestedBlockIndex = i;
					leastNumberOfRequests = currentNumberOfRequests; 
				}
			}
		}
		
		mRequestsPerBlockSemaphore.release();
		
		return leastRequestedBlockIndex;
	}

	/**
	 * Compares two Pieces and class them with their request (from the least-requested to the most-requested Piece).
	 * @param o Piece to compare with.
	 * @return
	 * <ul>
	 * <li> <0 if the current Piece is less requested than o
	 * <li> =0 if the current Piece is as requested as o
	 * <li> >0 if the current Piece is more requested than o
	 * </ul>
	 */
	public int compareTo(Piece o) {
		
		// Number of requests for each piece (respectively one for the current Piece and two for the Piece we want to compare to).
		double a = getAverageNumberOfRequestPairs();
		double b = o.getAverageNumberOfRequestPairs();
		
		return (a == b) ? 0 : ((a > b) ? 1 : -1);
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Fetch a block according to its index given in parameter.
	 * @param index of the requested block (not an offset).
	 * @return A block object if it exists or throws an exception
	 */
	public Block getBlock(int index) throws NoSuchElementException, NullHashException, BlockLengthException, WrongIndexException, IOException {
		
		if (!mReceivedBlockIndexes.contains(index)) // If the piece or the requested block are empty, leave.
			throw new NoSuchElementException("The requested block is empty.");
		
		if (mFileManager != null) return null;
		
		return mFileManager.read(this, index);
	}
	
	/**
	 * Returns the number of blocks in this piece
	 * @return number of blocks in this piece
	 */
	public int getBlockCount() {
		return mNbBlocks;
	}
	
	/**
	 * Test whether or not the piece is empty.
	 * @return true if the piece is empty. False otherwise.
	 */
	public boolean isEmpty() {
		return mReceivedBlockIndexes.isEmpty();
	}
	
	/**
	 * Test whether or not the piece is complete.
	 * @return true if the piece is complete. False otherwise.
	 */
	public boolean isComplete() {
		return mComplete;
	}
	
	/**
	 * Returns the index of the current Piece.
	 * @return index of the current Piece.
	 */
	public int getIndex() {
		return mIndex;
	}

	public int getSize() {
		return mSize;
	}
	
	public long getLeft() {
		long left = Block.BLOCK_SIZE * mReceivedBlockIndexes.size();
		
		if (mReceivedBlockIndexes.contains(mNbBlocks-1)) {
			left += Block.BLOCK_SIZE;
			left -= mSize - Block.BLOCK_SIZE * mNbBlocks;
		}
		
		return left; 
	}
	
	public int getBlockOffset(int index) {
		return index * Block.BLOCK_SIZE;
	}
	
	public int getBlockSize(int index) {
		return Math.min(Block.BLOCK_SIZE, mSize - Block.BLOCK_SIZE * index);
	}
}
