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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Wrapper class for the bottom part of the User Interface.
 * It contains four tabs : GeneralInfoTab, TrackerTab, PeersTab
 * and FilesTab.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -4439624160294340416L;
	
	// SubPanels into the InfoPanel.
	private GeneralInfoTab	tabGeneral = new GeneralInfoTab();
	private TrackerTab		tabTrackers = new TrackerTab();
	private FilesTab		tabFiles = new FilesTab();
	private PeersTab		tabPeers = new PeersTab();
	
	/**
	 * Main constructor
	 */
	public InfoPanel() {
		super(new BorderLayout());
		
		// We add them to the ObserversList that observe a change into TorrentListPanel.
		ObserversTorrentList.getInstance().addObserver(tabGeneral);
		ObserversTorrentList.getInstance().addObserver(tabTrackers);
		ObserversTorrentList.getInstance().addObserver(tabFiles);
		ObserversTorrentList.getInstance().addObserver(tabPeers);
		
		JScrollPane scrollPane;
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		
		// Create scrollPanes for the "Files" and "Peers" tabs then
		// add all the panels "General Info", "Trackers", "Peers" and "Files" to the tabbedPane.
		tabbedPane.addTab(Ressources.strings.general_info, tabGeneral);
		
		tabbedPane.addTab(Ressources.strings.tracker, tabTrackers);
		
		scrollPane = new JScrollPane(tabPeers);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());	
		tabbedPane.addTab(Ressources.strings.peers, scrollPane);
				
		scrollPane = new JScrollPane(tabFiles);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());
		tabbedPane.addTab(Ressources.strings.files, scrollPane);
		
	}
}
