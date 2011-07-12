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
import java.util.Date;
import eblast.bencoding.BEDictionary;
import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.Hash;
import eblast.checksum.NullHashException;

/**
 * This class parses a MetaInfo (.torrent) and allows 
 * the user to get all the informations about it.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 * @version 1.1 - 04.03.2011 - Modification because of the need of info_hash
 */
public class MetaInfo {
	// MetaInfo Dictionary
	static final String KEY_ANNOUNCE		= "announce";
	static final String KEY_INFO			= "info";
	
		// Optional keys
	static final String KEY_ANNOUNCE_LIST	= "announce-list";
	static final String KEY_CREATION_DATE	= "creation date";
	static final String KEY_CREATED_BY		= "created by";
	static final String KEY_ENCODING		= "encoding";
	static final String KEY_COMMENT			= "comment";
	
	// List of attributes of the MetaInfo
	private String			mAnnounce;
	private AnnounceList	mAnnounceList;
	private Info			mInfo;
	private Hash			mInfoHash;
	private long			mCreationDate;
	private String			mCreatedBy;
	private String			mEncoding;
	private String			mComment;

	/**
	 * Create an Instance of MetaInfo and initialize it
	 * @param dict MetaInfo Mapped Dictionary
	 * @throws MetaInfoException
	 * @throws InvalidBEncodingException An Exception has occurred during the process 
	 * @throws UnsupportedEncodingException 
	 * @throws NullHashException If the Hash is null.
	 */
	MetaInfo(BEDictionary dict, Hash infoHash) throws MetaInfoException, InvalidBEncodingException, UnsupportedEncodingException, NullHashException {
		mInfoHash = infoHash;
		
		// ----- Required Keys -----------------------------------------------
		if (dict.contains(KEY_ANNOUNCE))
			mAnnounce = dict.getString(KEY_ANNOUNCE);
		else
			throw new MetaInfoRequiredKeyException(KEY_ANNOUNCE);
		
		if (dict.contains(KEY_INFO))
			mInfo = new Info(dict.getDictionnary(KEY_INFO));
		else
			throw new MetaInfoRequiredKeyException(KEY_INFO);
		// -------------------------------------------------------------------
		
		// ----- Optional keys -----------------------------------------------
		if (dict.contains(KEY_ANNOUNCE_LIST))
			mAnnounceList = new AnnounceList(dict.getList(KEY_ANNOUNCE_LIST));
		
		if (dict.contains(KEY_CREATION_DATE))
			mCreationDate = dict.getLong(KEY_CREATION_DATE);

		if (dict.contains(KEY_CREATED_BY))
			mCreatedBy = dict.getString(KEY_CREATED_BY);

		if (dict.contains(KEY_ENCODING))
			mEncoding = dict.getString(KEY_ENCODING);

		if (dict.contains(KEY_COMMENT))
			mComment = dict.getString(KEY_COMMENT);
		// -------------------------------------------------------------------
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * @return The Tracker Announce represented by a String.
	 */
	public String getAnnounce() {
		return mAnnounce;
	}
	
	/**
	 * @return The Announce-list represented by an AnnounceList class.
	 */
	public AnnounceList getAnnounceList() {
		return mAnnounceList;
	}
	
	/**
	 * @return The Info Dictionary represented by an Info class.
	 */
	public Info getInfo() {
		return mInfo;
	}
	
	/**
	 * @return The Info Hash represented by a Hash class.
	 */
	public Hash getInfoHash() {
		return mInfoHash;
	}
	
	/**
	 * @return When the MetaInfo has been created.
	 */
	public Date getCreationDate() {
		return new Date(mCreationDate*1000);
	}

	/**
	 * @return Who has created the MetaInfo.
	 */
	public String getCreatedBy() {
		return mCreatedBy;
	}

	/**
	 * @return Encoding used to generate the Piece Part of the MetaInfo.
	 */
	public String getEncoding() {
		return mEncoding;
	}
	
	/**
	 * @return Comment added to describe the MetaInfo.
	 */
	public String getComment() {
		return mComment;
	}
}