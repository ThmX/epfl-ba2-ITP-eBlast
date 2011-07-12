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

package eblast.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import eblast.checksum.Hash;
import eblast.checksum.NullHashException;
import eblast.torrent.piece.Block;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.PieceLengthException;
import eblast.torrent.piece.WrongIndexException;

public class PieceTest {

    private static MessageDigest shaDigest;

    @BeforeClass
    public static void setup() {
    	
        try {
            shaDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Your Java runtime doesn't support SHA-1 digests - that's incredible but we can't continue.", e);
        }
    }

	@Test
    public void testCheckCorrectData() throws BlockLengthException, NullHashException, UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] data = new byte[Block.BLOCK_SIZE * 4];
        Random r = new Random();
        r.nextBytes(data);
        
        Hash hash = new Hash(shaDigest.digest(data));
        Piece piece=null;
		try {
			piece = new Piece(null, 2, data.length, hash);
		} catch (PieceLengthException e1) {}

		// Not used in our implementation
        //assertNull(piece.getData());
		
        assertEquals(0, (int)piece.getDownloadCompleteness());

        try {
			piece.feed(Block.BLOCK_SIZE * 0, new Block(Arrays.copyOfRange(data, Block.BLOCK_SIZE * 0, Block.BLOCK_SIZE * 1)));
			assertEquals(25, (int)piece.getDownloadCompleteness());
			
	        piece.feed(Block.BLOCK_SIZE * 3, new Block(Arrays.copyOfRange(data, Block.BLOCK_SIZE * 3, Block.BLOCK_SIZE * 4)));
	        assertEquals(50, (int)piece.getDownloadCompleteness());
	        
	        piece.feed(Block.BLOCK_SIZE * 1, new Block(Arrays.copyOfRange(data, Block.BLOCK_SIZE * 1, Block.BLOCK_SIZE * 2)));
	        assertEquals(75, (int)piece.getDownloadCompleteness());
	        
			piece.feed(Block.BLOCK_SIZE * 0, new Block(Arrays.copyOfRange(data, Block.BLOCK_SIZE * 0, Block.BLOCK_SIZE * 1)));
			assertEquals(75, (int)piece.getDownloadCompleteness());// Duplicate receive should not increment counter
	        
	        piece.feed(Block.BLOCK_SIZE * 2, new Block(Arrays.copyOfRange(data, Block.BLOCK_SIZE * 2, Block.BLOCK_SIZE * 3)));
	        assertEquals(100, (int)piece.getDownloadCompleteness());
	        
		} catch (WrongIndexException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		// Not activated when we don't have the FileManager
        //assertTrue(piece.isComplete());
        //assertTrue(piece.check());
        
        // Not used in our implementation
        //assertArrayEquals(data, piece.getData());
    }

    @Test(expected = NullPointerException.class)
    public void testCheckIncorrectData() throws BlockLengthException, NullHashException, NoSuchElementException, IOException, NoSuchAlgorithmException {
        byte[] data = "Dummy data Dummy data 252".getBytes();
        Hash hash = new Hash(shaDigest.digest(data));
        Piece piece=null;
		try {
			piece = new Piece(null, 2, 25, hash);
		} catch (PieceLengthException e1) {}

        byte[] block = "Dummy data Dummy data 253".getBytes();
        
        try {
			piece.feed(0, new Block(block));
		} catch (WrongIndexException e) {
			e.printStackTrace();
		}

        // the check is done automatically when the piece is 'completed'
        assertEquals(0, (int)piece.getDownloadCompleteness());
        assertTrue(!piece.isComplete());
        
        try {
			piece.feed(0, new Block(data));
		} catch (WrongIndexException e) {}
        assertEquals(true, piece.check());
        
        try {
			piece.feed(0, new Block(block));
		} catch (WrongIndexException e) {}
        assertEquals(true, piece.check());
        // if we give an already checked piece some wrong data, it must not be checked
    }
}