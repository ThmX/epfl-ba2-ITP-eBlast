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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eblast.gui.menus.PopupMouseMenu;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentManager;

/**
 * List that represents all the Torrents that we are downloading/uploading, but actually
 * contains TorrentItem items.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class TorrentListPanel extends JList implements ListCellRenderer, ListSelectionListener, ActionListener, MouseListener {

	private static final long serialVersionUID = -4439624160294340416L;
	
	private DefaultListModel	mModel;
	private TorrentManager		mTorrentManager;
	private int 				mPreviousListHash;
	
	/**
	 * Default constructor
	 */
	public TorrentListPanel() {
		super();
		
		mTorrentManager = TorrentManager.getInstance(); // We use it for getting all the available torrents.
		
		// Add listeners
		MainUI.getTimer().addActionListener(this);
		addListSelectionListener(this);
		addMouseListener(this);
		
		// Multiple selection, Vertical layout.
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setCellRenderer(this);
		
		mModel = new DefaultListModel();
		setModel(mModel);
		
		for (Torrent t : mTorrentManager.getTorrents()) {
			mModel.addElement(new TorrentItem(t)); // Add all the TorrentItems to the list
		}
		
		mPreviousListHash = mTorrentManager.getTorrentListHash(); // Get the list hash of all the torrents into the TManager.
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		TorrentItem item = (TorrentItem) value;
		
		// Alternate the background color of the TorrentItem in the list
		item.setBackground(isSelected ? Ressources.colors.selected : (index % 2 == 1) ? Ressources.colors.zebraPattern_1 : Ressources.colors.zebraPattern_2);
		
		return item;
	}
	
	/**
	 * Returns all the Torrents that have been selected in this Panel.
	 * @return all the Torrent objects that have been selected.
	 */
	public TorrentItem[] getSelectedTorrentItems() {
		
		Object[] selectedObjects = getSelectedValues(); // Get all the objects selected into this Torrent Panel.
		
		if (selectedObjects.length == 0) return null; // If there is no object select, return null.
		
		TorrentItem[] selectedTorrentItems = new TorrentItem[selectedObjects.length];
		
		// Returns the selected Torrents.
		for (int i = 0; i < selectedObjects.length; i++) {
			selectedTorrentItems[i] = (TorrentItem) selectedObjects[i];
		}
		return selectedTorrentItems;
	}

	/**
	 * Used when the selection in this List is changed.
	 */
	public void valueChanged(ListSelectionEvent e) {
		
		// When the selection is changed, give to all of the tabs the selection.
		for (int i = 0; i < ObserversTorrentList.getInstance().length(); i++) {
			
			// Transforms all the torrentItems into Torrents.
			Torrent[] torrents = null;
			if(getSelectedTorrentItems() != null) {
				torrents = new Torrent[getSelectedTorrentItems().length];
				for (int j = 0; j < getSelectedTorrentItems().length; j++) {
					torrents[j] = getSelectedTorrentItems()[j].getTorrent();
				}
			}
			
			// Transmits the torrents to all the tabs.
			ObserversTorrentList.getInstance().getObserver(i).update(torrents);
		}
	}

	/**
	 * What to do when the timer ticks
	 */
	public void actionPerformed(ActionEvent e) {
		
		int listHash = mTorrentManager.getTorrentListHash(); // Current hash of the list.
		
		// One or more torrents have been added/removed, we reload the model
		if (mPreviousListHash != listHash) {
			
			int[] selectedIndices = getSelectedIndices(); // get all the indices of the selected TorrentItems
			
			mModel.clear(); // Clear the model
			for (Torrent t : mTorrentManager.getTorrents()) {
				mModel.addElement(new TorrentItem(t));}		// Relod the model
			setModel(mModel);
			
			setSelectedIndices(selectedIndices);		// Select back all the TorrentItems
			
			mPreviousListHash = listHash;
		}
		
		// Updates the List of torrents.
		for (int i = 0; i < mModel.size(); i++) {
			
			((TorrentItem)mModel.get(i)).update();
		}
		
		repaint();
	}

	/**
	 * The mouse has been pressed
	 */
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) { // Check if the user wants the mouse menu.
			doPop(e);
		}
	}
	
	/**
	 * The mouse has been released
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) { // Check if the user wants the mouse menu.
			doPop(e);
		}
	}
	
	/**
	 * Right click popup menu.
	 * @param e MouseEvent event.
	 */
	public void doPop(MouseEvent e) {
		
		if (getSelectedTorrentItems() == null) return; // If there is no item selected, quit.
		
		PopupMouseMenu menu  = new PopupMouseMenu();
		
		// Creates the first menu depending on the current State of the Torrent.
		menu.setTorrentMode(getSelectedTorrentItems()[0].getTorrent().getTorrentState());
		
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	// Not implemented methods from interfaces
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
