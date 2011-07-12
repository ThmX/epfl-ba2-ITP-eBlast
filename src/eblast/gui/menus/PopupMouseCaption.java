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

package eblast.gui.menus;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import eblast.gui.Ressources;

/**
 * Represents a caption that illustrates what
 * are the color used in our program (down in the GeneralInfoTab)
 * to represent what we have downloaded so far.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class PopupMouseCaption extends JPopupMenu {

	private static final long serialVersionUID = 5829153173430232918L;
	
	private JPanel mCaption; // main panel
	
	/**
	 * Default constructor.
	 */
	public PopupMouseCaption() {
		
		mCaption = new CaptionPanel();
		add(mCaption);
	}
	
	
	/**
	 * This class is the actual caption with all the colors in it.
	 */
	private class CaptionPanel extends JPanel {
		
		private static final long serialVersionUID = -2057684202870992873L;

		/**
		 * Default constructor.
		 */
		public CaptionPanel() {
			super(true);
			
			// Create layout and borders
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
			
			// Create two main panels : one for the colors on the left and one for the text on the right
			JPanel colors = new JPanel(); colors.setLayout(new BoxLayout(colors, BoxLayout.Y_AXIS));
			JPanel txt = new JPanel(); txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
			
			// Add custom borders to the Color panel
			colors.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			
			// Color labels
			colors.add(createLabel(Color.white));
			colors.add(createLabel(Ressources.colors.blue_end));
			colors.add(createLabel(Ressources.colors.red_end));
			colors.add(createLabel(Ressources.colors.green_end));
			
			
			// Txt labels
			txt.add(createLabel(" : Unavailable"));
			txt.add(createLabel(" : Available"));
			txt.add(createLabel(" : Currently downloading"));
			txt.add(createLabel(" : Downloaded"));
			
			// Add the two panels to the Caption
			add(colors);
			add(txt);
		}
		
		/**
		 * This method generates a label of the color given in parameter.
		 * @param color background color of our future label.
		 * @return a JLabel of the given color
		 */
		private JLabel createLabel(Color color) {
			final Dimension dim = new Dimension(15, 15);
			
			JLabel label = new JLabel();
			
			label.setOpaque(true);
			label.setBackground(color);
			
			label.setMinimumSize(dim);
			label.setMaximumSize(dim);
			label.setPreferredSize(dim);
			
			label.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			
			return label;
		}
		
		/**
		 * This method generates a label containing the given text
		 * @param str text of our future label.
		 * @return a JLabel containing the given text
		 */
		private JLabel createLabel(String str) {
			final Dimension dim = new Dimension(160, 15);
			
			JLabel label = new JLabel(str);
			
			label.setMinimumSize(dim);
			label.setMaximumSize(dim);
			label.setPreferredSize(dim);
			
			return label;
		}
	}
}
