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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import eblast.torrent.Torrent;
import eblast.torrent.peer.Peer;

/**
 * This class contains informations about the peers that are downloading/uploading
 * a selected Torrent. It is into the TabbedPane at the third tab.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class PeersTab extends JPanel implements ActionListener, UpdateSelection {

	private static final long serialVersionUID = -8354089228601843588L;
	
	private JTable mTable;
	private DefaultTableModel mModel;
	
	private Torrent mSelectedTorrent; // Stores the selected Torrent for updating the panel with the list of peers.
	
	private static final String[] TABLE_COLUMN_NAMES = {
			Ressources.strings.ip,
			Ressources.strings.client,
			Ressources.strings.percent_symb
		};
		
	@SuppressWarnings("serial")
	public PeersTab() {
		super();
		
		MainUI.getTimer().addActionListener(this); // Add an actionListener to the timer.
		
		// Set layout and borders
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());
		
		mTable = new JTable();
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTable.setRowSelectionAllowed(true);
		
		// Make the Default Model non-editable by overriding the isCellEditable(int row, int column) method
		// of DefaultTableModel (normally it "returns true regardless of parameter values.").
		mModel = new DefaultTableModel() {

		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		mModel.setColumnIdentifiers(TABLE_COLUMN_NAMES);
		
		mTable.setModel(mModel);
		
		add(mTable.getTableHeader(), BorderLayout.PAGE_START);
		add(mTable, BorderLayout.CENTER);
	}
	
	/**
	 * Clear all data contained in this panel.
	 */
	public void clear() {
		
		while (mModel.getRowCount() > 0) {
			mModel.removeRow(0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Torrent[] torrentSelection) {
		
		// Cannot be selected.
		mTable.setFocusable(false);
		mTable.setCellSelectionEnabled(false);
		
		if (torrentSelection != null) {
			
			mSelectedTorrent = torrentSelection[0]; // We take the first entry that has been selected.
		}
	}

	/**
	 * What to do when the timer ticks
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (mSelectedTorrent != null) {
		
			clear(); // Resets the model.

			List<Peer> peersList = mSelectedTorrent.getConnectedPeers();
			synchronized (peersList) {
				String clientID = "";
				for (Peer currentPeer : peersList) {
					clientID = currentPeer.getID();
					// Get a part of the clientID if not empty, otherwise display that there is no client ID.
					clientID = currentPeer.getID().equals("") ? "N/A" : currentPeer.getID().substring(1, 7);
					
					String perc = Double.toString( (int)(100 * currentPeer.getPercent()) / 100.0  );
					mModel.addRow(new String[]{currentPeer.toString(), clientID, perc});
				}				
			}
		}
	}
}
