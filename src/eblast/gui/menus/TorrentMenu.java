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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eblast.gui.actions.TorrentActions;

/**
 * Main menu "Torrent" on top of the program.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class TorrentMenu extends JMenu {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -4002601651497977004L;
	
	private static final String MENU_NAME = "Torrent";
	private static final String RESUME_NAME = "Resume All";
	private static final String PAUSE_NAME = "Pause All";
	
	private JMenuItem mResumeAllItem;
	private JMenuItem mPauseAllItem;
	
	/**
	 * Main Constructor
	 */
	public TorrentMenu() {
		
		super(MENU_NAME);
		
		// Creates a "ResumeAll" JMenuItem, add a "ResumeAll" action and add it to the main menu.
		mResumeAllItem = new JMenuItem(RESUME_NAME);
		mResumeAllItem.addActionListener(new TorrentActions.ResumeAll());
		add(mResumeAllItem);

		// Creates a "PauseAll" JMenuItem, add a "PauseAll" action and add it to the main menu.		
		mPauseAllItem = new JMenuItem(PAUSE_NAME);
		mPauseAllItem.addActionListener(new TorrentActions.PauseAll());
		add(mPauseAllItem);
	}
}
