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

package eblast.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the Objects that need to know informations about the TorrentList in the GUI.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class ObserversTorrentList {

	// Singleton
	private static ObserversTorrentList mSingleton = new ObserversTorrentList();
	public static ObserversTorrentList getInstance() {
	
		return mSingleton;
	}
	
	// Observers list.
	private List<UpdateSelection> mObserversList = new ArrayList<UpdateSelection>();
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Add an observer that want to be notified of changes into the TorrentList.
	 * @param observer observer that want to be notified of changes into the TorrentList.
	 */
	public void addObserver(UpdateSelection observer) {
		mObserversList.add(observer);
	}
	
	/**
	 * Returns the observer at the index given in parameter
	 * @param index index of the observer that we have to return
	 * @return observer at the index given in parameter
	 */
	public UpdateSelection getObserver(int index) {
		return mObserversList.get(index);
	}
	
	/**
	 * Return all the observers.
	 * @return all the observers.
	 */
	public List<UpdateSelection> getAllObservers() {
		
		return mObserversList;
	}
	
	/**
	 * Return the total number of observers
	 * @return total number of observers
	 */
	public int length() {
		return mObserversList.size();
	}
}
