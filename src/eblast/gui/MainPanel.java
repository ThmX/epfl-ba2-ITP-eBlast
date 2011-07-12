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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * Main JPanel that contains the two main other Panels :
 * TorrentListPanel (on top) and InfoPanel (at the bottom).
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = -4439624160294340416L;
	
	private TorrentListPanel mTorrentListPanel;
	private InfoPanel		 mInfoPanel;
	
	private MainToolBar mToolBar;
	
	/**
	 * Creates the panel.
	 */
	public MainPanel() {
		super(new BorderLayout());
		
		// ----- ToolBar -----
		mToolBar = new MainToolBar();
		add(mToolBar, BorderLayout.PAGE_START);
		
		// ----- SplitPane (horizontal separation in the middle of the main Panel) ----- 
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		splitPane.setDividerSize(6);
		add(splitPane, BorderLayout.CENTER);
		
		mTorrentListPanel = new TorrentListPanel();
		mInfoPanel = new InfoPanel();
		
		splitPane.setLeftComponent(new JScrollPane(mTorrentListPanel)); // Torrent List
		splitPane.setRightComponent(mInfoPanel); // Info Panel
	}
}
