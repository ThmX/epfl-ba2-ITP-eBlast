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

package eblast.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import eblast.checksum.NullHashException;
import eblast.torrent.Torrent;
import eblast.torrent.piece.Block;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.Piece;
import eblast.torrent.piece.WrongIndexException;

/**
 * Class that is going to read/write blocks associated to
 * a torrent into a file.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.04.2011 - Initial version
 * @version 1.1 - 23.05.2011 - 
 */
// TODO Adapt to MultiFile
public class FileManager {
	
	private long mPieceSize;
	private List<TorrentFile> mFiles; // list of the files contained into the torrent (not the actual one, just containers).
	private File mDownloadDir;
	
	/**
	 * Default constructor.
	 * @param torrent torrent that is going to contain an instance of this class.
	 * @throws IOException if something went wrong with the I/O.
	 */
	public FileManager(Torrent torrent) throws IOException {
		
		mPieceSize = torrent.getPieceLength();
		mFiles = torrent.getFiles();
		mDownloadDir = new File(torrent.getDownloadDir());
		
		// Creates all the TorrentFile physically on the disk.
		for (TorrentFile tf: mFiles) {
			tf.open(mDownloadDir);
		}
	}
	
	/**
	 * Allow to close all files.
	 */
	public void close() {
		for (TorrentFile tf : mFiles) {
			tf.close();
		}
	}
	
	/**
	 * Allow to erase all files from the disk.
	 */
	public void erase() {
		for (TorrentFile tf : mFiles) {
			tf.erase();
		}
	}

	/**
	 * Read all blocks of a piece from the disk.
	 * @param piece piece from where we are going to read.
	 * @param idx block index
	 */
	public Block read(Piece piece, int idx) throws IOException, BlockLengthException, NullHashException, WrongIndexException {
		
		// XXX Change when multi-file
		RandomAccessFile raf = mFiles.get(0).getRandomAccessFile();

		raf.seek(piece.getIndex() * mPieceSize + idx * Block.BLOCK_SIZE);
		
		byte[] buffer = new byte[piece.getBlockSize(idx)];
		
		@SuppressWarnings("unused")
		int len = raf.read(buffer);

		return new Block(buffer);
	}
	
	/**
	 * Writes the specified block on the disk.
	 * @param piece Piece where the block is contained
	 * @param idx block index
	 * @throws IOException
	 */
	public void write(Piece piece, int idx, Block block) throws IOException {
		// XXX Change when multi-file
		RandomAccessFile raf = mFiles.get(0).getRandomAccessFile();

		raf.seek(piece.getIndex() * mPieceSize + idx * Block.BLOCK_SIZE);
		raf.write(block.toBytes());
	}
}