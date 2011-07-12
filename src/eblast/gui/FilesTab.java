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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import eblast.Convertor;
import eblast.io.TorrentFile;
import eblast.torrent.Torrent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Displays all the files contained into the Torrent.
 * This class is in the TabbedPanel at the 4th tab.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class FilesTab extends JPanel implements ActionListener, UpdateSelection {

	private static final long serialVersionUID = -8354089228601843588L;
	
	private JTable mTable;
	private DefaultTableModel mModel;
	
	// Stores the selected Torrent for updating the panel with the list files into the torrent.
	private Torrent mSelectedTorrent;
	
	private static final String[] TABLE_COLUMN_NAMES = {
			Ressources.strings.download_path,
			Ressources.strings.size,
			Ressources.strings.percent_symb
		};
		
	@SuppressWarnings("serial")
	public FilesTab() {
		
		super();
		
		// Borders and Layout
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());
		
		MainUI.getTimer().addActionListener(this); // Add an actionListener to the timer.
		
		mTable = new JTable();
		
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // One line at a time
		mTable.setRowSelectionAllowed(true); // Select a whole row in the JTable
		
		// Make the Default Model non-editable by overriding the isCellEditable(int row, int column) method
		// of DefaultTableModel (normally it "returns true regardless of parameter values.").
		mModel = new DefaultTableModel() {

		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		
		mModel.setColumnIdentifiers(TABLE_COLUMN_NAMES);
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		mTable.setModel(mModel);
		
		// Add the table to the FilesTab Panel.
		add(mTable.getTableHeader(), BorderLayout.PAGE_START);
		add(mTable, BorderLayout.CENTER);
	}
	
	/**
	 * Clears the model.
	 */
	public void clear() {
		
		for (int i = 0; i < mModel.getRowCount(); i++) {
			mModel.removeRow(i);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Torrent[] torrentSelection) {

		// Cannot be selected.
		mTable.setFocusable(false);
		mTable.setCellSelectionEnabled(false);
		
		if (torrentSelection != null) { // If torrentItems are selected
			
			mSelectedTorrent = torrentSelection[0]; // We take the first entry that has been selected.
		}
		else {
			mSelectedTorrent = null; // We clean the selection
		}
	}

	/**
	 * What to do when the timer ticks.
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		if (mSelectedTorrent != null) { // If there is at least one torrent selected
			
			clear(); // Resets the model.
			
			List<TorrentFile> filesList = mSelectedTorrent.getFiles();
			
			for (TorrentFile currentFile : filesList) {
				
				//TODO Replace the last value ("b") when multi-file is implemented.
				mModel.addRow(new String[]{currentFile.getPath(), String.valueOf(Convertor.formatBytes(currentFile.length(), 0, "")), "b"});
			}
		}
	}
}
