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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eblast.Convertor;
import eblast.gui.menus.PopupMouseCaption;
import eblast.torrent.Torrent;

/**
 * This class contains General informations about the selected Torrent.
 * It is into the TabbedPane at the first tab.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public class GeneralInfoTab extends JPanel implements ActionListener, UpdateSelection {

	private static final long serialVersionUID = 3349863195067471644L;

	/**
	 * Top panel that has all the general informations about the Torrent.
	 */
	private static class GeneralInfoTopPanel extends AbstractLabelPanel {

		private static final long serialVersionUID = -8354089268601843588L;
	
		private JLabel mNameLabel;
		private JLabel mAuthorLabel;
		private JLabel mCommentLabel;
		private JLabel mDateLabel;
		private JLabel mSizeLabel;
		private JLabel mPathLabel;
		private JLabel mPiecesCountLabel;
		private JLabel mPiecesSizeLabel;
		
		/**
		 * Main Constructor.
		 */
		public GeneralInfoTopPanel() {
			super();
			
			// Labels
			mNameLabel = newLabel(Ressources.strings.name);
			mAuthorLabel = newLabel(Ressources.strings.author);
			mCommentLabel = newLabel(Ressources.strings.comment);
			mDateLabel = newLabel(Ressources.strings.date);
			mSizeLabel = newLabel(Ressources.strings.size);
			mPathLabel = newLabel(Ressources.strings.download_path);
			mPiecesCountLabel = newLabel(Ressources.strings.piece_count);
			mPiecesSizeLabel = newLabel(Ressources.strings.piece_size);
			
			clear();
		}
		
		/**
		 * This method updates the left panel with the informations on the torrent.
		 * @param torrent torrent with which we are going to update the panel.
		 */
		public void updatePanel(Torrent torrent) {
			
			mNameLabel.setText(torrent.toString()); // Set the name
			
			mAuthorLabel.setText(torrent.getCreatedBy());
			if (torrent.getCreatedBy() == null) { // If there is no author, display "N/A".
				mAuthorLabel.setText("N/A");
			}
			
			mCommentLabel.setText(torrent.getComment()); // Set the commentary
			if (torrent.getComment() == null) {
				mCommentLabel.setText("N/A");
			}
			
			mDateLabel.setText(torrent.getCreationDate().toString()); // Set the creation date
			if (torrent.getCreationDate() == null) {
				mDateLabel.setText("N/A");
			}
			
			// The rest of the options
			mSizeLabel.setText(String.valueOf(Convertor.formatBytes(torrent.getLength(), 0, "")));
			mPathLabel.setText(torrent.getDownloadDir());
			mPiecesCountLabel.setText(String.valueOf(torrent.getPieceCount()));
			mPiecesSizeLabel.setText(String.valueOf(Convertor.formatBytes(torrent.getPieceLength(), 0, "")));
		}
		
		/**
		 * Clear all the labels into this panel.
		 */
		public void clear() {
			mNameLabel.setText("");
			mAuthorLabel.setText("");
			mCommentLabel.setText("");
			mDateLabel.setText("");
			mSizeLabel.setText("");
			mPathLabel.setText("");
			mPiecesCountLabel.setText("");
			mPiecesSizeLabel.setText("");
		}
	}
	
	/**
	 * Panel where the Piece downloading is represented graphically.
	 */
	private static class DownloadedPanel extends JPanel implements MouseListener, MouseMotionListener {
		
		private static final long serialVersionUID = -731927353298033619L;
		
		// Used for Statitics
		private Set<Integer> mReceivedPieces;
		private Set<Integer> mAvailablePieces;
		private Set<Integer> mRequestedPieces;
		private int mNbPieces;
		
		private PopupMouseCaption mCaption; // Caption used when the mouse is over this panel.
		
		/**
		 * Default Constructor
		 */
		public DownloadedPanel() {
			super();
			
			mCaption = new PopupMouseCaption();
			
			/*
			 * Used for displaying the caption the way it does.
			 */
			addMouseListener(this);
			addMouseMotionListener(this);
			
			// Set Borders and size
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(getWidth(), 30));
		}
		
		/**
		 * Updates this Panel with the informations into the given torrent
		 * @param torrent torrent with which we are going to update all informations.
		 */
		public void updatePanel(Torrent torrent) {
			
			if (torrent != null) {
				mNbPieces = torrent.getPieceCount();
				mReceivedPieces = torrent.getReceivedPieces();
				mAvailablePieces = torrent.getAvailablePieces();
				mRequestedPieces = torrent.getPieceManager().getRequestedPieces();
				
			} else { reset(); }
			
			repaint();
		}
		
		/**
		 * If there is no torrent selected, clear the Downloaded panel. 
		 */
		public void reset() {
			mNbPieces = 0;
			mReceivedPieces = new HashSet<Integer>();
			mAvailablePieces = new HashSet<Integer>();
		}
		
		/**
		 * Paint method where the DownloadedPanel is drawed
		 */
		public void paint(Graphics g) {
			super.paint(g);
			
			int width = getWidth()-3;
			int height = getHeight()-3;
			
			g.setColor(Color.white);
			g.fillRect(1, 1, width, height);

			if (mNbPieces <= 0) return;
			
			double width_per_piece = (double)width / mNbPieces;
			Graphics2D g2 = (Graphics2D)g;
			GradientPaint gp;
			
			// ----- Print available pieces -----
			gp = new GradientPaint(
					0, 0, Ressources.colors.blue_start,
					width, 0, Ressources.colors.blue_end
				);
			
			g2.setPaint(gp);
			
			synchronized (mAvailablePieces) {
				for (int i: mAvailablePieces) {
					g.fillRect((int)(width_per_piece*i+1), 1, (int)Math.ceil(width_per_piece), height);
				}
			}
			
			// ----- Print requested pieces -----
			gp = new GradientPaint(
					0, 0, Ressources.colors.red_start,
					width, 0, Ressources.colors.red_end
				);
			
			g2.setPaint(gp);
			
			synchronized (mRequestedPieces) {
				for (int i: mRequestedPieces) {
					g.fillRect((int)(width_per_piece*i+1), 1, (int)Math.ceil(width_per_piece), height);
				}
			}
			
			// ----- Print received pieces -----
			gp = new GradientPaint(
					0, 0, Ressources.colors.green_start,
					width, 0, Ressources.colors.green_end
				);
			g2.setPaint(gp);
			
			synchronized (mReceivedPieces) {
				for (int i: mReceivedPieces) {
					g.fillRect((int)(width_per_piece*i+1), 1, (int)Math.ceil(width_per_piece), height);
				}
			}
			
		}

		/**
		 * When we enter the panel, we display the caption.
		 */
		public void mouseEntered(MouseEvent e) {
			
			mCaption.show(e.getComponent(), e.getX(), e.getY());
		}
		
		/**
		 * When we quit the panel, we hide the caption.
		 */
		public void mouseExited(MouseEvent e) {
			mCaption.setVisible(false);
		}
		
		/**
		 * When the mouse moves, we make the caption move with it.
		 */
		//TODO Avoid Flickering
		public void mouseMoved(MouseEvent e) {
			
			mCaption.setLocation(e.getXOnScreen(), e.getYOnScreen());
		}
		
		// Non-implemented methods from interfaces.
		public void mouseClicked(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
		public void mouseDragged(MouseEvent e) {}
	}
	
	private GeneralInfoTopPanel mTopPanel;
	private DownloadedPanel mDownloadedPanel;
	
	private Torrent mSelectedTorrent; // Stores the selected Torrent for updating the downloaded panel.
	
	/**
	 * Default Constructor.
	 */
	public GeneralInfoTab() {
		super();
		
		MainUI.getTimer().addActionListener(this); // Add an actionListener to the timer.
		
		setLayout(new BorderLayout()); // set default layout
		
		// Creates the top panel
		mTopPanel = new GeneralInfoTopPanel();
		add(mTopPanel, BorderLayout.CENTER);
		
		// Creates the bottom panel
		mDownloadedPanel = new DownloadedPanel();
		add(mDownloadedPanel, BorderLayout.PAGE_END);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Torrent[] torrentSelection) {
		
		Torrent selectedTorrent = null;
		if (torrentSelection != null) {
			
			selectedTorrent = torrentSelection[0]; // We take the first entry that has been selected.
			mTopPanel.updatePanel(selectedTorrent);
			
			mSelectedTorrent = selectedTorrent;
		} else {
			mSelectedTorrent = null; // No torrent selected.
			mTopPanel.clear(); // Clear all data contained in this panel.
		}
	}

	/**
	 * What to do when the timer ticks
	 */
	public void actionPerformed(ActionEvent e) {
		
		mDownloadedPanel.updatePanel(mSelectedTorrent);
	}
	

}
