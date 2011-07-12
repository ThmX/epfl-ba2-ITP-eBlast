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

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eblast.Convertor;
import eblast.torrent.Torrent;

/**
 * Represents an item into the TorrentListPanel.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class TorrentItem extends JPanel {

	private static final long serialVersionUID = -7401822363128043420L;
	
	private Torrent mTorrent; // Actual torrent stored into this item.
	
	private JLabel mIcon;
	private JLabel mName;
	private JLabel mStatus;
	private double mProgress;
	
	private Color mColorStart;
	private Color mColorEnd;

	/**
	 * Default constructor.
	 * @param torrent torrent linked to this item
	 */
	public TorrentItem(Torrent torrent) {
		super();
		
		mTorrent = torrent;
		
		// Set layout and borders
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // Left to right.
		setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0.2f)));
		
		mIcon = new JLabel(); add(mIcon);
		
		// Constructing the JPanel that is going to represent this item
		JPanel pane = new JPanel();
		pane.setBackground(new Color(0, 0, 0, 1.0f));
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.setOpaque(false);
		add(pane);
		
		
		// Set the name of the torrentItem and some other things (font, opacity)
		mName = new JLabel("mName"); mName.setOpaque(false); mName.setFont(new Font(getFont().getName(), Font.BOLD, 14));
		pane.add(mName);
		
		mStatus = new JLabel("mStatus - mProgression"); mStatus.setOpaque(false); pane.add(mStatus);

		//---------------------------------------------
		
		update(); // Updates the current item.
	}
	
	/**
	 * Create and set the information string that is going to be displayed for the current torrent
	 * (status, DL/UL Rate, percentage) according to its current status.
	 * @return the string that is going to be displayed.
	 */
	private String generateStatusString() {
		
		double download = mTorrent.getSpeed().download;
		double upload = mTorrent.getSpeed().upload;
		double progress = (int) (100 * mProgress) / 100.0;
		
		StringBuilder sb = new StringBuilder();
		
		switch (mTorrent.getTorrentState()) {
		
		case started:
			mColorStart = Ressources.colors.half_blue_start;
			mColorEnd = Ressources.colors.half_blue_end;
			
			mIcon.setIcon( Ressources.drawables.file_down );
			sb.append(Ressources.strings.started);
			sb.append(" - dl: ");
			sb.append(Convertor.formatBytes(download, 2, "/s"));
			sb.append(" - ul: ");
			sb.append(Convertor.formatBytes(upload, 2, "/s"));
			sb.append(" - ");
			sb.append(Convertor.formatBytes(mTorrent.getLength() - mTorrent.getLeft(), 0, ""));
			sb.append(" of ");
			sb.append(Convertor.formatBytes(mTorrent.getLength(), 0, ""));
			break;
			
		case stopped:
			mColorStart = Ressources.colors.half_gray_start;
			mColorEnd = Ressources.colors.half_gray_end;
			
			mIcon.setIcon( Ressources.drawables.file_pause );
			sb.append(Ressources.strings.stopped);
			
			break;
		case completed:
			mColorStart = Ressources.colors.half_green_start;
			mColorEnd = Ressources.colors.half_green_end;
			
			mIcon.setIcon( Ressources.drawables.file_up );
			sb.append(Ressources.strings.completed);
			sb.append(" - ul: ");
			sb.append(Convertor.formatBytes(upload, 2, "/s"));
			break;
			
		case checking:
			mColorStart = Ressources.colors.half_red_start;
			mColorEnd = Ressources.colors.half_red_end;
			
			mIcon.setIcon( Ressources.drawables.file_checking );
			sb.append(Ressources.strings.checking);
			break;
		}
		
		sb.append(" - ");
		sb.append(progress);
		sb.append(" ");
		sb.append(Ressources.strings.percent_symb);
		
		return sb.toString();
	}
	
	/**
	 * Updates the current TorrentItem.
	 */
	public void update() {
		
		mProgress = mTorrent.getCompleteness();
		mName.setText(mTorrent.toString());
		mStatus.setText(generateStatusString());

		repaint();
	}
	
	/**
	 * Draws the progress bar behind the TorrentItem with a gradient color
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		int width = (int)(getWidth() * mProgress / 100);
		
		g2.setPaint(new GradientPaint(0, 0, mColorStart, getWidth(), 0, mColorEnd));
		g.fillRect(0, 0, width, getHeight());
		
		// Draw the line only if it's necessary
		if (mProgress < 100.0) {
			g.setColor(Color.darkGray);
			g.drawLine(width, 0, width, getHeight());
		}
	}

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the torrent associated to this item.
	 * @return torrent associated to this item.
	 */
	public Torrent getTorrent() {
		return mTorrent;
	}
}
