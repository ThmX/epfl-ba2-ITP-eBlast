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
import java.util.List;

import eblast.bencoding.BEDictionary;
import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.NullHashException;
import eblast.io.TorrentFile;

/**
 * This class reads the Info value of a MetaFile (.torrent)
 * and allows the user to get all the information about it.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class FileDictionnary {
	static final String KEY_LENGTH			= "length";
	static final String KEY_MD5SUM			= "md5sum";
	static final String KEY_PATH			= "path";
	
	private BEDictionary	mDictionary;
	
	// List of attributes of the Info Dictionary
	private int				mLength;
	private String			mMD5;	
	private List<String>	mPath;

	/**
	 * Create an Instance of Info and initialize it
	 * @param dict Info Mapped Dictionary
	 * @throws MetaInfoException if an error has occurred with the MetaInfo
	 * @throws InvalidBEncodingException a value isn't on the good type
	 * @throws UnsupportedEncodingException if the system doesn't support UTF-8
	 * @throws NullHashException if the Hash is null
	 */
	public FileDictionnary(BEDictionary dict) throws MetaInfoException, InvalidBEncodingException, UnsupportedEncodingException, NullHashException {
		mDictionary = dict;

		// ----- Required Keys -----------------------------------------------
		if (mDictionary.contains(KEY_LENGTH))
			mLength = mDictionary.getInt(KEY_LENGTH);
		else
			throw new MetaInfoRequiredKeyException(KEY_LENGTH);
		
		if (mDictionary.contains(KEY_PATH))
			mPath = mDictionary.getList(KEY_PATH).getStrings();
		else
			throw new MetaInfoRequiredKeyException(KEY_PATH);
		
		// -------------------------------------------------------------------

		// Optional Key
		if (mDictionary.contains(KEY_MD5SUM))
			mMD5 = mDictionary.getString(KEY_MD5SUM);
	}
	
	/**
	 * Returns a list of Torrent File. 
	 * @return a list of Torrent File
	 * @throws UnsupportedEncodingException 
	 */
	public TorrentFile getFile() throws UnsupportedEncodingException {
		return new TorrentFile(mLength, mPath, mMD5);
	}
}