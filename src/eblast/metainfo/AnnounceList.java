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

import java.util.List;

import eblast.bencoding.BEList;
import eblast.bencoding.InvalidBEncodingException;

/**
 * Implements the announce-list as described on the specification:<br>
 * <a href="http://bittorrent.org/beps/bep_0012.html"/>http://bittorrent.org/beps/bep_0012.html</a>
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class AnnounceList {
	
	BEList mLists;
	
	/**
	 * Create an Instance of MetaFile and initialize it
	 * @param file the File of the torrent
	 * @throws InvalidBEncodingException An Exception has occurred during the process 
	 */
	AnnounceList(BEList lists) {
		mLists = lists;
	}
	
	/**
	 * Number of Announces there is into this list
	 * @return number of Announces there is into this list
	 */
	public int size() {
		return mLists.size();
	}
	
	/**
	 * As described on the specification, the idx corresponds to a
	 * backup number of the announce. Here is an example:<br>
	 * d['announce-list'] = [ [tracker1, tracker2], [backup1], [backup2] ]
	 * @param idx index of the backup needed. Use <b>idx=0</b> to get the default announce list
	 * @return A list of String representing each tracker announce
	 * @throws InvalidBEncodingException 
	 */
	public List<String> get(int idx) throws InvalidBEncodingException {
		return mLists.getList(idx).getStrings();
	}
}