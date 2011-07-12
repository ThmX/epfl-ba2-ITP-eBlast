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

import org.junit.Assert;
import org.junit.Test;

import eblast.checksum.NullHashException;
import eblast.io.TorrentFile;
import eblast.metainfo.MetaInfo;
import eblast.metainfo.MetaInfoException;
import eblast.metainfo.MetaInfoReader;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentException;
import eblast.torrent.piece.Block;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.WrongIndexException;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TorrentTest {

	@Test
	public void testReadFromFileGivenDataFile() throws IOException, MetaInfoException, TorrentException, NullHashException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		MetaInfo metainfo = MetaInfoReader.openMetaInfo("trunk/data/LePetitPrince.torrent");
		Torrent torrent = Torrent.createTorrent(metainfo);

		assertEquals("All pieces must have been read from the data file", 100.0, torrent.getCompleteness(), 0.0);
		assertTrue("All pieces must have been read from the data file", torrent.isComplete());
	}

	@Test
	public void testWriteToFile() throws IOException, MetaInfoException, TorrentException, NoSuchAlgorithmException, NullHashException, BlockLengthException, WrongIndexException {
		MetaInfo metainfo = MetaInfoReader.openMetaInfo("trunk/data/LePetitPrince.torrent");
		Torrent torrent = Torrent.createTorrent(metainfo);

		long pieceLength = torrent.getPieceLength();
		int pieceCount = torrent.getPieceCount();

		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < pieceCount; i++) {
			indices.add(i);
		}
		Collections.shuffle(indices);
		Assert.assertEquals(pieceCount, indices.size());
		Assert.assertEquals(pieceLength, 1 << 15);

		RandomAccessFile file = new RandomAccessFile("trunk/data/st_exupery_le_petit_prince.pdf", "r");
		for (int index : indices) {
			Piece piece = torrent.getPieces().get(index);

			// do the second block first, if any
			int secondBlockSize = piece.getSize() - Block.BLOCK_SIZE;
			if (secondBlockSize > 0) {
				file.seek(index * pieceLength + Block.BLOCK_SIZE);
				byte[] block = new byte[secondBlockSize];
				file.read(block);
				piece.feed(Block.BLOCK_SIZE, new Block(block));
			}

			// now do the first block
			{
				int firstBlockSize = Math.min(Block.BLOCK_SIZE, piece.getSize());
				file.seek(index * pieceLength);
				byte[] block = new byte[firstBlockSize];
				file.read(block);
				piece.feed(0, new Block(block));
			}
		}
		file.close();

		TorrentFile otfile = torrent.getFiles().get(0);
		File outputFile = new File("trunk/data/" + otfile.getPath());
		assertTrue(
				"The written file must be identical to the data file",
				checkMD5checksum("0D41D08C0D908F000B2040E9080090980EC0F8427E", outputFile)
			);

	}

	private boolean checkMD5checksum(String original, File file)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		try {
			is = new DigestInputStream(is, md);
		} finally {
			is.close();
		}

		return original.equals(toHexString(md.digest()));
	}

	private String toHexString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for (byte b : input) {
			if (b < 16)
				sb.append("0");
			sb.append(Integer.toHexString(0xFF & b).toUpperCase());
		}
		return sb.toString();
	}
}