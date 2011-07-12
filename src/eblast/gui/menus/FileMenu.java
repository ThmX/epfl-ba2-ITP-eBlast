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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eblast.gui.ConfigDialog;
import eblast.gui.Ressources;
import eblast.gui.actions.TorrentActions;
import eblast.torrent.TorrentManager;

/**
 * Main menu "File" on top of the program
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class FileMenu extends JMenu {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -4002601651497977004L;
	
	private JMenuItem mOpenItem;
	private JMenuItem mConfigItem;
	private JMenuItem mExitItem;

	/**
	 * Default constructor.
	 */
	public FileMenu() {
		super(Ressources.strings.file);
		
		// Creates a "file" menuItem and add an "open" action to it. 
		mOpenItem = new JMenuItem(Ressources.strings.open_torrent);
		mOpenItem.addActionListener(new TorrentActions.Open());
		add(mOpenItem);
		
		addSeparator();

		// Creates a "config" menuItem and add an "openConfigDialog" action to it.
		mConfigItem = new JMenuItem(Ressources.strings.configurations);
		mConfigItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new ConfigDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		add(mConfigItem);
		
		addSeparator();
		
		// Creates an "exit" menuItem and add an "exit" action to it.
		mExitItem = new JMenuItem(Ressources.strings.exit);
		mExitItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				TorrentManager.getInstance().closeTorrents();
				System.exit(0);
			}
		});
		
		add(mExitItem);
		
	}
}
