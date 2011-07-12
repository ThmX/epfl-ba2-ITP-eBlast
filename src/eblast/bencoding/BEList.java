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

import java.util.ArrayList;
import java.util.List;

import eblast.bencoding.BEValue;
import eblast.bencoding.InvalidBEncodingException;

/**
 * This class create a List from a BEValue.
 * It allows to use more easily the BEValue List.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class BEList {
	private List<BEValue> mList;

	/**
	 * Create an Instance of the list from a List of BEValue
	 * @param list the list of BEValues.
	 * @throws NullPointerException If the BEValue is null. 
	 * @throws InvalidBEncodingException 
	 */
	public BEList(BEValue list) throws NullPointerException, InvalidBEncodingException {
		if (list == null) throw new NullPointerException("The list can't be null.");
		mList = list.getList();
	}
	
	/**
	 * @return The number of contained object in the list.
	 */
	public int size() {
		return mList.size();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a String.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't a String.
	 */
	public String getString(int idx) throws InvalidBEncodingException {
		return mList.get(idx).getString();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into an Ineger.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't an Ineger.
	 */
	public int getInt(int idx) throws InvalidBEncodingException {
		return mList.get(idx).getInt();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a Long Integer.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't a Long Integer.
	 */
	public long getLong(int idx) throws InvalidBEncodingException {
		return mList.get(idx).getLong();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into an Array of Bytes.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't a Array of Bytes.
	 */
	public byte[] getBytes(int idx) throws InvalidBEncodingException {
		return mList.get(idx).getBytes();
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a List.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't a List.
	 */
	public BEList getList(int idx) throws InvalidBEncodingException {
		return new BEList( mList.get(idx) );
	}
	
	/**
	 * Get the value of the key mapped, and convert it into a Map.
	 * @param idx Index of the value
	 * @return The value in the list at the position <i>idx</i>. 
	 * @throws InvalidBEncodingException This bevalue isn't a Map.
	 */
	public BEDictionary getDictionnary(int idx) throws InvalidBEncodingException {
		return new BEDictionary( mList.get(idx) );
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		for (BEValue bevalue: mList) {
			try {
				strings.add(bevalue.getString());
				
			} catch (InvalidBEncodingException e) {
				// Do nothing
			}
		}
		return strings;
	}
	
	public List<BEList> getLists() {
		List<BEList> lists = new ArrayList<BEList>();
		for (BEValue bevalue: mList) {
			try {
				lists.add(new BEList(bevalue));
				
			} catch (InvalidBEncodingException e) {
				// Do nothing
			}
		}
		return lists;
	}
	
	public List<BEDictionary> getDictionnaries() {
		List<BEDictionary> dictionnaries = new ArrayList<BEDictionary>();
		for (BEValue bevalue: mList) {
			try {
				dictionnaries.add(new BEDictionary(bevalue));
				
			} catch (InvalidBEncodingException e) {
				// Do nothing
			}
		}
		return dictionnaries;
	}
}