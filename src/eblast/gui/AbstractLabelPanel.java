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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;

/**
 * This class Creates a label with title.
 * 
 * Example : |Title Value| : Single label with two entries
 * 
 * JLabel test = newLabel(Title) returns a reference to Value, 
 * to change value we do a test.setText(Value)
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 */
abstract public class AbstractLabelPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final Font FONT_BOLD = new Font(getFont().getName(), Font.BOLD, getFont().getSize());
	
	private JPanel paneLabelTitle;	// left panel
	private JPanel paneLabel;		// right panel 
	
	/**
	 * Default Constructor
	 */
	public AbstractLabelPanel() {
		super();
		
		// Set borders and layout
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEtchedBorder());
		
		// Creates the title label (left)
		paneLabelTitle = new JPanel();
		paneLabelTitle.setLayout(new BoxLayout(paneLabelTitle, BoxLayout.Y_AXIS));
		paneLabelTitle.setAlignmentY(Component.TOP_ALIGNMENT);
		paneLabelTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(paneLabelTitle);
		
		// Creates the Value label (right)
		paneLabel = new JPanel();
		paneLabel.setLayout(new BoxLayout(paneLabel, BoxLayout.Y_AXIS));
		paneLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		paneLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(paneLabel);		
	}
	
	/**
	 * This method creates a JLabel with two entries
	 * @param name title of the label
	 * @return reference to the second part (the right one) of the label.
	 */
	protected JLabel newLabel(String name) {
		JLabel label = new JLabel(name);
		JLabel labelTitle = new JLabel(name);
		
		label.setMaximumSize(new Dimension(400, 20));
		labelTitle.setMaximumSize(new Dimension(200, 20));
		labelTitle.setFont(FONT_BOLD);
		
		paneLabelTitle.add(labelTitle);
		paneLabel.add(label);
		
		return label;
	}

}
