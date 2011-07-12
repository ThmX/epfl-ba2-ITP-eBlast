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

package eblast.metainfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eblast.bencoding.BEDictionary;
import eblast.bencoding.BEList;
import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.Hash;
import eblast.checksum.NullHashException;
import eblast.io.TorrentFile;

/**
 * This class reads the Info value of a MetaFile (.torrent)
 * and allows the user to get all the informations about it.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class Info {
	
	// MetaInfo Dictionary
	static final String KEY_PIECE_LENGTH	= "piece length";
	static final String KEY_PIECES			= "pieces";
	static final String KEY_PRIVATE			= "private";
	
	// File(s)
	static final String KEY_NAME			= "name";
	static final String KEY_LENGTH			= "length";
	static final String KEY_MD5SUM			= "md5sum";
		// Multi
	static final String KEY_FILES			= "files";
	static final String KEY_PATH			= "path";
	
	static final int SHA1_STRING_LENGTH	= 20;
	
	private BEDictionary		mDictionary;
	
	// List of attributes of the Info Dictionary
	private long				mLength;
	private long				mPieceLength;
	private ArrayList<Hash>		mPieces;
	private boolean				mPrivate;
	private String				mName;
	private ArrayList<TorrentFile>		mFiles;

	/**
	 * Create an Instance of Info and initialize it.
	 * @param dict info Mapped Dictionary
	 * @throws MetaInfoException if an error has occurred with the MetaInfo
	 * @throws InvalidBEncodingException a value isn't on the good type
	 * @throws UnsupportedEncodingException if the system doesn't support UTF-8
	 * @throws NullHashException if the Hash is null
	 */
	Info(BEDictionary dict) throws MetaInfoException, InvalidBEncodingException, UnsupportedEncodingException, NullHashException {
		mDictionary = dict;

		// ----- Required Keys -----------------------------------------------
		if (mDictionary.contains(KEY_PIECE_LENGTH))
			mPieceLength = mDictionary.getInt(KEY_PIECE_LENGTH);
		else
			throw new MetaInfoRequiredKeyException(KEY_PIECE_LENGTH);
		
		if (mDictionary.contains(KEY_PIECES))
			parsePieces();
		else
			throw new MetaInfoRequiredKeyException(KEY_PIECES);

		if (mDictionary.contains(KEY_NAME))
			mName = mDictionary.getString(KEY_NAME);
		else
			throw new MetaInfoRequiredKeyException(KEY_NAME);
		// -------------------------------------------------------------------

		// Optional Key
		if (mDictionary.contains(KEY_PRIVATE))
			mPrivate = mDictionary.getInt(KEY_PRIVATE) == 1; // if (private == 1) => It is a Private Torrent.
		
		// With one or several files, we stock them in an ArrayList.
		mFiles = new ArrayList<TorrentFile>();
		
		// the Key Files is there only for multi-files mode.
		if (mDictionary.contains(KEY_FILES))
			parseMultFile();
		else
			parseSingleFile();
		
		mLength = 0;
		for (TorrentFile file: mFiles) {
			mLength += file.length();
		}
	}
	
	/**
	 * This method parses the dictionary in single-file mode.
	 * @throws InvalidBEncodingException a value isn't on the good type
	 * @throws UnsupportedEncodingException the system doesn't support UTF-8
	 */
	private void parseSingleFile() throws InvalidBEncodingException, UnsupportedEncodingException {
		int length = mDictionary.getInt(KEY_LENGTH);
		String md5 = mDictionary.contains(KEY_MD5SUM) ? mDictionary.getString(KEY_NAME) : null;
		ArrayList<String> name = new ArrayList<String>();
		name.add(mName);
		
		mFiles.add(new TorrentFile(length, name, md5));
	}
	
	/**
	 * This method parse the Dictionary in multi-file mode.
	 * @throws InvalidBEncodingException A value isn't on the good type
	 * @throws UnsupportedEncodingException If the System doesn't support UTF-8
	 * @throws MetaInfoException 
	 * @throws NullHashException 
	 */
	private void parseMultFile() throws InvalidBEncodingException, UnsupportedEncodingException, NullHashException, MetaInfoException {
		BEList list = mDictionary.getList(KEY_FILES);
		for (BEDictionary bedict: list.getDictionnaries()) {
			FileDictionnary dict = new FileDictionnary(bedict);
			mFiles.add(dict.getFile());
		}
	}
	
	/**
	 * This method parses the dictionary to create the Pieces Hash
	 * @throws MetaInfoException If the MetaInfo is wrong
	 * @throws InvalidBEncodingException If the System doesn't support UTF-8
	 * @throws UnsupportedEncodingException 
	 * @throws NullHashException 
	 */
	private void parsePieces() throws MetaInfoException, InvalidBEncodingException, NullHashException, UnsupportedEncodingException {
		
		mPieces = new ArrayList<Hash>();
		
		byte[] pieces = mDictionary.getBytes(KEY_PIECES);
		if (pieces.length % SHA1_STRING_LENGTH != 0) {
			
			throw new MetaInfoException("The SHA1 length should be a multiple of 20 bytes.");
		}
		
		for (int i=0; i<pieces.length; i+=SHA1_STRING_LENGTH) {
			
			mPieces.add(new Hash(Arrays.copyOfRange(pieces, i, i+SHA1_STRING_LENGTH)));
		}
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns true if the torrent is private, false otherwise.
	 * @return true if the torrent is private, false otherwise
	 */
	public boolean isPrivate() {
		return mPrivate;
	}
	
	/**
	 * Returns the number of bytes of all files.
	 * @return number of bytes of all files
	 */
	public long getLength() {
		return mLength;
	}
	
	/**
	 * Returns the number of bytes in each piece.
	 * @return number of bytes in each piece
	 */
	public long getPieceLength() {
		return mPieceLength;
	}
	
	/**
	 * Return a lists of Hashes of the Pieces.
	 * @return list of Hashes of the Pieces
	 */
	public List<Hash> getPiecesHashes() {
		return mPieces;
	}
	
	/**
	 * @return Depends on the File Mode:
	 * <ul>
	 * 		<li> <b>Single:</b> File Name
	 * 		<li> <b>Multi:</b> Directory Name
	 * </ul> 
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @return A list of Torrent File.
	 */
	public List<TorrentFile> getFiles() {
		return new ArrayList<TorrentFile>(mFiles);
	}
}