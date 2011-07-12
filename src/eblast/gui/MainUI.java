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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import eblast.gui.menus.MainMenu;
import eblast.torrent.TorrentManager;

/**
 * Root of the user Interface : this class Launches the whole User Interface.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class MainUI extends JFrame implements WindowListener, ActionListener {

	private static final long serialVersionUID = 2190220027067259951L;
	
	private MainPanel			mMainPanel; // Main panel
	private static Timer		mTimer = null; // Timer of the program.

	/**
	 * Creates the main JFrame.
	 */
	public MainUI() {
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	// We'll use a WindowListener to close the UI.
		setBounds(0, 0, 800, 600);								// 800x600
		setLocationRelativeTo(null);							// Center of the screen
		setTitle(Ressources.strings.eblast);
		addWindowListener(this);
		
		// Start the timer that refresh all the UI
		mTimer = new Timer(Ressources.numbers.timer_delay, this);
		mTimer.start();
		
		// Add the main Panel
		mMainPanel = new MainPanel();
		setContentPane(mMainPanel);
		
		// Add the main Menu
		setJMenuBar(new MainMenu());
	}

	// When we close the window, ask the user if he's sure he wants to quit.
	public void windowClosing(WindowEvent e) {
		int selected_option = JOptionPane.showConfirmDialog(
									this,
									"Are you sure?",
									"Exit",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE
								);
		
		if (selected_option == JOptionPane.YES_OPTION) { // He wants to quit...
			TorrentManager.getInstance().closeTorrents(); // Delete torrents and quit program
			System.exit(0);
		}
	}
	
	// Implemented for the WindowsListener Interface)
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	/**
	 * What to do when the timer ticks
	 */
	public void actionPerformed(ActionEvent e) {}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the main Timer.
	 */
	public static Timer getTimer() {
		return mTimer;
	}
}
