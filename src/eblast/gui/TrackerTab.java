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
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import eblast.torrent.Torrent;
import eblast.torrent.tracker.TrackerInfo;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Displays all the Trackers used for a torrent.
 * This class is in the TabbedPanel at the 2th tab.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class TrackerTab extends JPanel implements UpdateSelection, ActionListener {

	private static final long serialVersionUID = -8354089228601843588L;
	
	/**
	 * This JPanel displays a general list (with addresses only) of all the
	 * trackers available for this torrent. 
	 */
	private static class TrackerInfoPanel extends AbstractLabelPanel {

		private static final long serialVersionUID = -8354089268601843588L;

		private JLabel mNameLabel;
		private JLabel mSeederLabel;
		private JLabel mLeecherLabel;
		private JLabel mLastUpdateLabel;
		private JLabel mStatusLabel;
		
		/**
		 * Default constructor.
		 */
		public TrackerInfoPanel() {
			super();
			
			// Labels
			mNameLabel = newLabel(Ressources.strings.name);
			mSeederLabel = newLabel(Ressources.strings.seeder);
			mLeecherLabel = newLabel(Ressources.strings.leecher);
			mLastUpdateLabel = newLabel(Ressources.strings.last_update);
			mStatusLabel = newLabel(Ressources.strings.status);
		}
		
		/**
		 * Updates all the labels contained in this class with the tracker given in parameter
		 * @param tracker tracker from which we are going to extract all the informations to update all the labels
		 */
		public void updateTrackerInfoPanel(TrackerInfo tracker) {
			
			long lastUp = (new Date().getTime() - tracker.getLastUpdate()) / 1000;
			
			mNameLabel.setText(tracker.toString());
			mSeederLabel.setText(String.valueOf(tracker.getSeedersNumber()));
			mLeecherLabel.setText(String.valueOf(tracker.getLeechersNumber()));
			mLastUpdateLabel.setText(String.valueOf(lastUp) + " seconds");
			mStatusLabel.setText(tracker.getErrorStatus());
		}
		
		/**
		 * Clear the labels
		 */
		public void clear() {
			mNameLabel.setText("");
			mSeederLabel.setText("");
			mLeecherLabel.setText("");
		}
	}

	private JList mList;
	private DefaultListModel mModel;
	private TrackerInfoPanel mPaneTrackerInfos;
	private Torrent mSelectedTorrent; // Stores the selected Torrent for updating the panel with the list of trackers.
	
	/**
	 * Main constructor
	 */
	public TrackerTab() {
		super();
		
		MainUI.getTimer().addActionListener(this); // Add an actionListener to the timer.
		
		setLayout(new GridLayout(1, 2)); // Set a default layout
		
		// new list with Single selection, Vertical orientation
		mList = new JList();		
		mList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mList.setLayoutOrientation(JList.VERTICAL);
		mList.setCellRenderer(new ListCellRenderer() {

			/**
			 * {@inheritDoc}
			 */
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel(value.toString());
				label.setOpaque(true);
				label.setBackground(isSelected ? Ressources.colors.selected : (index % 2 == 1) ? Ressources.colors.zebraPattern_1 : Ressources.colors.zebraPattern_2);
				return label;
			}
			
		});
		
		JScrollPane scrollPane = new JScrollPane(mList);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());
		add(scrollPane);
		
		mModel = new DefaultListModel();
		mList.setModel(mModel); // sets a model of items for the List
		
		mPaneTrackerInfos = new TrackerInfoPanel();
		add(mPaneTrackerInfos);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Torrent[] torrentSelection) {
		
		Torrent selectedTorrent = null;
		if (torrentSelection != null) {
			
			selectedTorrent = torrentSelection[0]; // We take the first entry that has been selected.
			
			mSelectedTorrent = selectedTorrent;
		} else {
			mSelectedTorrent = null;
			mPaneTrackerInfos.clear(); // Clear all data contained in this panel.
		}
	}

	/**
	 * Updates at each tick of the Timer.
	 */
	public void actionPerformed(ActionEvent e) {
		
		
		if (mSelectedTorrent != null) {
			
			int[] indexes = mList.getSelectedIndices(); // Saves the selection because otherwise it will unselect all the trackers.
			
			mModel.clear(); // Deletes the previous Trackers.
			
			// Create the list on the left with all the trackers.
			for (TrackerInfo currentTracker : mSelectedTorrent.getTrackers()) {
				
				mModel.addElement(currentTracker);
			}
			
			mList.setSelectedIndices(indexes);
			
			// If we selected one item into the list of trackers.
			if (mList.getSelectedValue() != null && mList.getSelectedValues().length == 1) {
				mPaneTrackerInfos.updateTrackerInfoPanel((TrackerInfo) mList.getSelectedValue());
			}
		
		}
	}
}
