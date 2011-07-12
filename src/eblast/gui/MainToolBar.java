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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import eblast.gui.actions.TorrentActions;

/**
 * This toolbar is the main one in the Program, directly
 * in the interface.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class MainToolBar extends JToolBar {

	private static final long serialVersionUID = 5859995358350998826L;
	
	/**
	 * Type of Buttons that we will use with an associated image.
	 */
	private static class JToolBarButton extends JButton {
		
		private static final long serialVersionUID = -2801052865794622659L;
		
		private static final int HEIGHT = 25;
		
		public JToolBarButton(String str, Icon icon) {
			super(str, icon);
			this.setPreferredSize(new Dimension(super.getPreferredSize().width,HEIGHT));
			this.setMaximumSize(new Dimension(super.getPreferredSize().width,HEIGHT));
			this.setMinimumSize(new Dimension(super.getPreferredSize().width,HEIGHT));
		}
	}
	
	private JToolBarButton mOpenButton;
	private JToolBarButton mResumeButton;
	private JToolBarButton mPauseButton;
	private JToolBarButton mDeleteButton;

	/**
	 * Defaut constructor
	 */
	public MainToolBar() {
		super();
		
		setFloatable(false);
				
		mOpenButton = new JToolBarButton(Ressources.strings.open, Ressources.drawables.open);
		mResumeButton = new JToolBarButton(Ressources.strings.resume, Ressources.drawables.play);
		mPauseButton = new JToolBarButton(Ressources.strings.pause, Ressources.drawables.pause);
		mDeleteButton = new JToolBarButton(Ressources.strings.delete, Ressources.drawables.delete);

		add(mOpenButton);
		addSeparator();
		add(mResumeButton);
		add(mPauseButton);
		add(mDeleteButton);
		
		// Connect each button to a specific action.
		mOpenButton.addActionListener(new TorrentActions.Open());
		mPauseButton.addActionListener(new TorrentActions.Pause());
		mResumeButton.addActionListener(new TorrentActions.Resume());
		mDeleteButton.addActionListener(new TorrentActions.Close());
	}
}
