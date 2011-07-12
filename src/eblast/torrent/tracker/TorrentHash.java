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

package eblast.torrent.tracker;

import java.util.Arrays;

import eblast.http.BinaryURLDecoder;
import eblast.http.BinaryURLEncoder;

/**
 * Represents the 20-byte hash of a torrent.
 * 
 * We need to wrap the byte array in a class in order to have
 * equals() and hashCode() implemented properly, so that we can
 * use it in a HashMap.
 *
 * @author Ismail Amrani
 * @author Bruno Didot
 * @author Amos Wenger
 *
 */
public class TorrentHash {

	private byte[] data;
	private String urlEncoded;
	
	public TorrentHash(byte[] data) {
		assert(data.length == 20);
		this.data = data;
		this.urlEncoded = BinaryURLEncoder.encode(data);
	}
	
	public TorrentHash(String urlEncodedHash) {
		this.data = BinaryURLDecoder.decode(urlEncodedHash);
		this.urlEncoded = urlEncodedHash;
		assert(data.length == 20);
	}
	
	public String urlEncoded() {
		return this.urlEncoded;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TorrentHash)) {
			return false;
		}
		
		TorrentHash th = (TorrentHash) o;
		return Arrays.equals(th.data, data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	public byte[] binaryHash() {
		return data;
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	@Override
	public String toString() {
		return Arrays.toString(data);
	}
}
