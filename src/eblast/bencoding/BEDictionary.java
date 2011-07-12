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

package eblast.bencoding;

import java.util.Map;
import eblast.bencoding.BEValue;
import eblast.bencoding.InvalidBEncodingException;

/**
 * This class create a Dictionary from a BEValue.
 * It allows to use more easily the BEValue Dictionary.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class BEDictionary {
	private Map<String, BEValue> mDictionary;

	/**
	 * Create an Instance of the dictionary from a mapped BEValue.
	 * @param dict Mapped Dictionary.
	 * @throws NullPointerException If the BEValue is null.  
	 */
	public BEDictionary(BEValue dict) throws NullPointerException, InvalidBEncodingException {
		if (dict == null) throw new NullPointerException("The dictionnary can't be null.");
		mDictionary = dict.getMap();
	}
	
	/**
	 * Check if the key is contained into the Dictionary.
	 * @param key Key in String
	 * @return True if it is contained, false otherwise.
	 */
	public boolean contains(String key) {
		return mDictionary.containsKey(key);
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a String.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't a String.
	 */
	public String getString(String key) throws InvalidBEncodingException {
		return mDictionary.get(key).getString();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into an Integer.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't an Integer.
	 */
	public int getInt(String key) throws InvalidBEncodingException {
		return mDictionary.get(key).getInt();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a Long Integer.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't a Long Integer.
	 */
	public long getLong(String key) throws InvalidBEncodingException {
		return mDictionary.get(key).getLong();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into an Array of Bytes.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't an Array of Bytes.
	 */
	public byte[] getBytes(String key) throws InvalidBEncodingException {
		return mDictionary.get(key).getBytes();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a List.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't a List.
	 */
	public BEList getList(String key) throws InvalidBEncodingException {
		return new BEList( mDictionary.get(key) );
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a Dictionary.
	 * @param key Key in String.
	 * @return The value of the mapped key.
	 * @throws InvalidBEncodingException This bevalue isn't a Dictionary.
	 */
	public BEDictionary getDictionnary(String key) throws InvalidBEncodingException {
		return new BEDictionary(mDictionary.get(key));
	}
}