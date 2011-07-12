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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eblast.metainfo.MetaInfo;
import eblast.metainfo.MetaInfoException;
import eblast.metainfo.MetaInfoReader;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentException;
import eblast.torrent.piece.Block;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.WrongIndexException;
import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.NullHashException;

public class UpdatePiecePriorityTest {

	@Test
	public void testPieceFeeding() throws InvalidBEncodingException, IOException, TorrentException, MetaInfoException, NullHashException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		
		MetaInfo metainfo = MetaInfoReader.openMetaInfo("trunk/data/LePetitPrince.torrent");
    	Torrent torrent = Torrent.createTorrent(metainfo);
		
		int pieceLength = torrent.getPieces().get(0).getSize();
		int pieceCount = torrent.getPieces().size();
		
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < pieceCount; i++) {
			indices.add(i);
		}
		Collections.shuffle(indices);
		Assert.assertEquals(pieceCount, indices.size());
		Assert.assertEquals(pieceLength, 1 << 15);
		
		RandomAccessFile file = new RandomAccessFile("trunk/data/st_exupery_le_petit_prince.pdf", "r");
		for(int index: indices) {
			Piece piece = torrent.getPieces().get(index);
			
			// do the second block first, if any
			int secondBlockSize = piece.getSize() - Block.BLOCK_SIZE;
			if(secondBlockSize > 0) {
				file.seek(index * pieceLength + Block.BLOCK_SIZE);
				byte[] block = new byte[secondBlockSize];
				file.read(block);
				
				try {
					piece.feed(Block.BLOCK_SIZE, new Block(block));
				} catch (NullHashException e) {}
				catch (BlockLengthException e) {}
				catch (WrongIndexException e) {}
			}
			
			// now do the first block
			{
				int firstBlockSize = Math.min(Block.BLOCK_SIZE, piece.getSize()); 
				file.seek(index * pieceLength);
				byte[] block = new byte[firstBlockSize];
				file.read(block);
				
				try {
					piece.feed(0, new Block(block));
				} catch (NullHashException e) {}
				catch (BlockLengthException e) {} 
				catch (WrongIndexException e) {}
			}
		}
		file.close();
		//torrent.getPieceManager().showProgress();
		torrent.getPieceManager().updatePriorities();
		Assert.assertTrue(torrent.isComplete());
	}
	
}