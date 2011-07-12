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
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import eblast.settings.EBlastSettings;
import eblast.torrent.TorrentManager;

/**
 * Dialog that represents the configuration window into the GUI.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 */
public class ConfigDialog extends JDialog implements KeyListener {

	private static final long serialVersionUID = -2470059818708284127L;
	
	private final JDialog dialog = this;
	private final JPanel contentPanel = new JPanel(); // main panel
	private JTextField tfDownDir;
	private JTextField tfPort;
	private JCheckBox cbEncryption;
	private JTextField tfMaxPeers;

	/**
	 * Main constructor.
	 */
	public ConfigDialog() {
		
		// Size and default layout of this dialog.
		setBounds(100, 100, 444, 206); 
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		final EBlastSettings settings = TorrentManager.getInstance().getSettings();
		
		JLabel lblDownloadDirectory = new JLabel("Download directory:");
		lblDownloadDirectory.setBounds(6, 12, 150, 16);
		contentPanel.add(lblDownloadDirectory);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(settings.getDownloadDir());
		
		tfDownDir = new JTextField(fc.getSelectedFile().getAbsolutePath());
		tfDownDir.setBounds(168, 6, 235, 28);
		contentPanel.add(tfDownDir);
		tfDownDir.setColumns(10);
		
		JButton btDownDir = new JButton("...");
		btDownDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
					tfDownDir.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btDownDir.setBounds(402, 9, 30, 22);
		contentPanel.add(btDownDir);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(6, 52, 150, 16);
		contentPanel.add(lblPort);
		
		tfPort = new JTextField( String.valueOf(settings.getPort()) );
		tfPort.addKeyListener(this);
		tfPort.setColumns(10);
		tfPort.setBounds(168, 46, 100, 28);
		contentPanel.add(tfPort);
		
		cbEncryption = new JCheckBox("Activate encryption");
		cbEncryption.setSelected( settings.isEncryptionActivated() );
		cbEncryption.setBounds(6, 114, 200, 23);
		contentPanel.add(cbEncryption);
		
		JLabel lblMaximumPeers = new JLabel("Maximum Peers:");
		lblMaximumPeers.setBounds(6, 86, 150, 16);
		contentPanel.add(lblMaximumPeers);
		
		tfMaxPeers = new JTextField( String.valueOf(settings.getMaxPeers()) );
		tfMaxPeers.addKeyListener(this);
		tfMaxPeers.setColumns(10);
		tfMaxPeers.setBounds(168, 80, 100, 28);
		contentPanel.add(tfMaxPeers);
		
		{
			
			JPanel buttonPane = new JPanel(); // Button panel
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.PAGE_END);
			
			{ // OK button
				JButton btOK = new JButton("OK");
				btOK.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						settings.setDownloadDir(fc.getSelectedFile());
						settings.setEncryption(cbEncryption.isSelected());
						settings.setMaxPeers(Integer.valueOf(tfMaxPeers.getText()));
						settings.setPort(Integer.valueOf(tfPort.getText()));
						try {
							TorrentManager.getInstance().relaunch();
						} catch (IOException e1) {}
						TorrentManager.getInstance().saveToXMLFile();
						
						dialog.setModal(false);
						dialog.setVisible(false);
					}
				});
				btOK.setActionCommand("OK");
				getRootPane().setDefaultButton(btOK);
				buttonPane.add(btOK);
			}
			
			{ // CANCEL button
				JButton btCancel = new JButton("Cancel");
				btCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.setModal(false);
						dialog.setVisible(false);
					}
				});
				btCancel.setActionCommand("Cancel");
				buttonPane.add(btCancel);
			}
		}
	}

	/**
	 * 
	 */
	public void keyTyped(KeyEvent e) {
		if (!Character.isDigit(e.getKeyChar())) {
			e.setKeyChar((char) 0);
		} else {
			if ((e.getSource() == tfPort) && !tfPort.getText().isEmpty()) {
				int port = Integer.valueOf(tfPort.getText() + e.getKeyChar());
				
				if ((port < 1) || (port > 65535)) {
					e.setKeyChar((char) 0);
				}
			}
		}
	}

	
	// Unused implemented methodes interface.
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

}
