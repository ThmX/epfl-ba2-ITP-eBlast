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

package eblast;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

import eblast.gui.MainUI;
import eblast.log.ConsoleLogger;
import eblast.log.Log;
import eblast.settings.XMLException;
import eblast.torrent.TorrentManager;

/**
 * This class is the main launcher of this program.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 08.03.2011 - Initial version
 */
public class EBlast {

	private static final String MAC_OSX_NAME = "Mac OS X";
	
	// -> /home/user/.eBlast/
	private static final String EBLAST_DIR = System.getProperty("user.home") + System.getProperty("file.separator") + ".eBlast" + System.getProperty("file.separator");
	private static final File XML_SETTINGS = new File(EBLAST_DIR + "config.xml");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		Log.addListener(new ConsoleLogger());
		Log.setDebugMode(false);
		
		try {
			TorrentManager.getInstance().load(XML_SETTINGS);
		} catch (XMLException e) {}
		catch (IOException e) {
			Log.e("Eblast:: IOException", "IO problem");
		}
		
		if (System.getProperty("os.name").equals(MAC_OSX_NAME)) {
			// Activate the OSX menu integration (if we are on OSX)
			System.setProperty("apple.laf.useScreenMenuBar", "true");

		}
		
		// Activate the Clearlooks linux
		try {
		    // Set the System LookAndFeel
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	Log.e("LookAndFeel", "Cannot able to activate the LookAndFeel.");
	    }
	    
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
