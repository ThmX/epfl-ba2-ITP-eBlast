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

package eblast.gui.menus;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import eblast.gui.actions.TorrentActions;
import eblast.torrent.Torrent;
import eblast.torrent.Torrent.TorrentStates;

/**
 * Represents a popup menu that appears when we do 
 * a right click. It contains options related to
 * Torrents, such as Pause, resume of delete.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class PopupMouseMenu extends JPopupMenu {
	
	private static final long serialVersionUID = 1L;
	
	JMenuItem mPauseResumeItem;
	JMenuItem mStopItem;
	
	/**
	 * Default constructor.
	 */
	public PopupMouseMenu() {
		
		mPauseResumeItem = new JMenuItem();
		
		// Creates the stop menu item.
		mStopItem = new JMenuItem("Delete this torrent");
		mStopItem.addActionListener(new TorrentActions.Close());
		
		add(mPauseResumeItem);
		add(mStopItem);
	}
	
	/**
	 * Sets the current state of the torrent.
	 * @param tm current state of the torrent (either PAUSE or RESUME are valid).
	 */
	public void setTorrentMode(Torrent.TorrentStates tm) {
		
		if (tm == TorrentStates.checking || tm == TorrentStates.completed) return; // Only pause or resume
		
		if (tm == TorrentStates.started) {
			mPauseResumeItem.setText("Pause");
			mPauseResumeItem.addActionListener(new TorrentActions.Pause());
		} else {
			mPauseResumeItem.setText("Resume");
			mPauseResumeItem.addActionListener(new TorrentActions.Resume());
		}
	}
}
