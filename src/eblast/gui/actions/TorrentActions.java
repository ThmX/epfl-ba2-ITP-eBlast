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

package eblast.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import eblast.checksum.NullHashException;
import eblast.gui.ObserversTorrentList;
import eblast.gui.Ressources;
import eblast.gui.UpdateSelection;
import eblast.log.Log;
import eblast.metainfo.MetaInfo;
import eblast.metainfo.MetaInfoException;
import eblast.metainfo.MetaInfoReader;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentException;
import eblast.torrent.TorrentManager;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.WrongIndexException;

/**
 * This class allows a user to perform actions on a Torrent :
 * Open, pause, resume or delete a Torrent.
 * This class contains is a mere wrapper that contains
 * inner classes, know as Actions.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class TorrentActions implements UpdateSelection {
	
	private static Torrent[] mTorrentSelection; // Contains all the torrent selected.
	
	/**
	 * Stores the selected torrent(s). If no torrents are selected, 
	 * it stores null.
	 */
	public void update(Torrent[] torrents) {
		mTorrentSelection = torrents;
	}
	
	/**
	 * This action ask the user to choose a torrent file.
	 */
	public static class Open implements ActionListener, UpdateSelection {

		/**
		 * Default Constructor.
		 */
		public Open() {
			
			// Connects us to the list of torrents to know what torrents are selected.
			ObserversTorrentList.getInstance().addObserver(this);
		}
		
		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			// ------------ Choose a torrent file only ----------------
			
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				
				public String getDescription() {
					return Ressources.strings.torrent_file;
				}
				
				public boolean accept(File file) {
					String fileName = file.getName();
					String ext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

					return ( ext.equalsIgnoreCase("torrent") ) ? true : file.isDirectory();
				}
			});
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
			
			// ------------ Choose a torrent file only ----------------
			
			int returnValue = fc.showOpenDialog(null);
			if(returnValue == JFileChooser.APPROVE_OPTION) { // If the user selected a file

				File file = fc.getSelectedFile(); // We get this file
				
				MetaInfo metaInfo = null;
				try {
					metaInfo = MetaInfoReader.openMetaInfo(file.getAbsolutePath()); // We parse it
				} catch (MetaInfoException e1) {
					
					Log.e("MainToolbar::Open torrent from file", "there is something wrong with the given metainfo : "+file.getName());
				} catch (FileNotFoundException e1) {
					
					Log.e("MainToolbar::Open torrent from file", "The file "+file.getName()+" has not been found.");
				} catch (IOException e1) {
					
					Log.e("MainToolbar::IO", "IO Exception");
				}
				
				try {
					Torrent currentTorrent = Torrent.createTorrent(metaInfo);	// We create the torrent...
					TorrentManager.getInstance().addTorrent(currentTorrent);	// And we add it to TorrentManager
					
				} catch (NoSuchAlgorithmException e1) {

					Log.e("MainToolbar::NoSuchAlgorithmException", "...");
				} catch (NullHashException e1) {
					
					Log.e("MainToolbar::Null Hash", "The hash is null.");
				} catch (TorrentException e1) {

					Log.e("MainToolbar::Torrent", "there is something wrong with the torrent file.");
				} catch (BlockLengthException e1) {

					Log.e("MainToolbar::Block length", "Wrong block length");
				} catch (WrongIndexException e1) {

					Log.e("MainToolbar::Wrong index", "Wrong index");
				} catch (IOException e1) {

					Log.e("MainToolbar::IO", "IO Exception");
				}
			}
			
			fc.setVisible(true);
		}

		/**
		 * Stores the selected torrent(s). If no torrents are selected, 
		 * it stores null.
		 */
		public void update(Torrent[] torrentSelection) {
			
			mTorrentSelection = torrentSelection;
		}
		
	}

	/**
	 * This action pauses selected Torrents.
	 */
	public static class Pause implements ActionListener, UpdateSelection {

		/**
		 * Default constructor.
		 */
		public Pause() {
			
			// Connects us to the list of torrents to know what torrents are selected.
			ObserversTorrentList.getInstance().addObserver(this);
		}
		
		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			if (mTorrentSelection == null) return; // Quit if no torrents are selected.
			
			// We stop all selected torrents.
			for (Torrent currentTorrent : mTorrentSelection) {
				currentTorrent.stopTorrent();
			}
		}

		/**
		 * Stores the selected torrent(s). If no torrents are selected, 
		 * it stores null.
		 */
		public void update(Torrent[] torrentSelection) {
			mTorrentSelection = torrentSelection;
		}
	}
	
	/**
	 * This action resumes selected torrents.
	 */
	public static class Resume implements ActionListener, UpdateSelection {

		/**
		 * Default constructor.
		 */
		public Resume() {
			
			// Connects us to the list of torrents to know what torrents are selected.
			ObserversTorrentList.getInstance().addObserver(this);
		}
		
		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			if (mTorrentSelection == null) return; // Quit if no torrents are selected.
			
			// We start all selected torrents.
			for (Torrent currentTorrent : mTorrentSelection) {
				currentTorrent.startTorrent();
			}
		}

		/**
		 * Stores the selected torrent(s). If no torrents are selected, 
		 * it stores null.
		 */
		public void update(Torrent[] torrentSelection) {
			mTorrentSelection = torrentSelection;
		}
	}
	
	/**
	 * This action deletes selected torrents.
	 */
	public static class Close implements ActionListener, UpdateSelection {

		/**
		 * Default constructor.
		 */
		public Close() {
			
			// Connects us to the list of torrents to know what torrents are selected.
			ObserversTorrentList.getInstance().addObserver(this);
		}
		
		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			if (mTorrentSelection == null) return; // Quit if no torrents are selected.
				
			String[] choices = {"delete and erase" , "delete", "Cancel"}; // Choices
			
			String question = "Are you sure you want to delete this torrent ?";
			if(mTorrentSelection.length > 1) {
				question = "Are you sure you want to delete these torrents ?";
			}
			
			// Display the Option dialog, return 0 when delete+erase and 1 when juste delete
			int selected_option = JOptionPane.showOptionDialog(
					null,
					question ,
					"Delete torrents",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					choices,
					0
				);
			
			if ((selected_option == 0) || (selected_option == 1)) { 
				for (int i = 0; i < mTorrentSelection.length; i++) { // Delete all the selected torrents.
					TorrentManager.getInstance().removeTorrent(mTorrentSelection[i].getInfoHash(), selected_option == 0);
				}
			}
		}

		/**
		 * Stores the selected torrent(s). If no torrents are selected, 
		 * it stores null.
		 */
		public void update(Torrent[] torrentSelection) {
			mTorrentSelection = torrentSelection;
		}
	}
	
	/**
	 * This action pauses all available torrents.
	 */
	public static class PauseAll implements ActionListener {

		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			for (Torrent currentTorrent : TorrentManager.getInstance().getTorrents()) {
				currentTorrent.stopTorrent();
			}
		}
	}
	
	/**
	 * This action resumes all available torrents.
	 */
	public static class ResumeAll implements ActionListener {

		/**
		 * Main action.
		 */
		public void actionPerformed(ActionEvent e) {
			
			for (Torrent currentTorrent : TorrentManager.getInstance().getTorrents()) {
				currentTorrent.startTorrent();
			}
		}
	}
}