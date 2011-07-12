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

package eblast.test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import eblast.log.ConsoleLogger;
import eblast.log.FileLogger;
import eblast.log.Log;
import eblast.metainfo.MetaInfo;
import eblast.metainfo.MetaInfoException;
import eblast.metainfo.MetaInfoReader;
import eblast.settings.XMLException;
import eblast.torrent.Torrent;
import eblast.torrent.TorrentException;
import eblast.torrent.TorrentManager;
import eblast.torrent.piece.BlockLengthException;
import eblast.torrent.piece.WrongIndexException;
import eblast.torrent.tracker.TrackerInfo;
import eblast.torrent.tracker.TrackerInfoException;
import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.NullHashException;

public class Download {
	
	public Torrent torrent;

	/** Used for testing mostly - max number of peers we attempt to download from */
	public static int MAX_PEERS = 80;
	
	public static void main(String[] args) throws InvalidBEncodingException, IOException, NullHashException, MetaInfoException, TorrentException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		
		Date now = new Date();
		Log.addListener(new ConsoleLogger());
		FileLogger fl = new FileLogger("trunk/data/eblast-" + now.getTime() + ".log" );
		Log.addListener(fl);
		Log.setDebugMode(true);
		
		new Download().run((new Random()).nextInt(30000) + 6881, "trunk/data/LePetitPrince.torrent", true, true);
		//new Download().run(29485, "trunk/data/debian-6.0.1a-i386-businesscard.iso.torrent", false, false);
		//new Download().run(29485, "trunk/data/ubuntu-11.04-alternate-i386.iso.torrent", false, false);
		//new Download().run(29485, "trunk/data/archlinux-2010.05-netinstall-i686.iso.torrent", false, false);
	}

	public void run(int port, String path, boolean ownTracker, boolean enableEncryption) throws InvalidBEncodingException, IOException, MetaInfoException, NullHashException, TorrentException, BlockLengthException, WrongIndexException, NoSuchAlgorithmException {
		final File XML_SETTINGS = new File("/Users/thmx/.eBlast/config.xml");
		
		// ----- TorrentManager Configuration ----- 
		
		TorrentManager torrentManager = TorrentManager.getInstance();
		try {
			torrentManager.load(XML_SETTINGS);
		} catch (XMLException e) {
			e.printStackTrace();
		}
		
		// ----- Torrent Creation -----
		
		MetaInfo metainfo = MetaInfoReader.openMetaInfo(path);
		torrent = Torrent.createTorrent(metainfo);
		
		if (ownTracker) {
			torrent.getTrackers().clear();
			try {
				torrent.getTrackers().add(new TrackerInfo(torrent, "http://localhost:6969/announce"));
			} catch (TrackerInfoException e) {}
		}
		
		torrentManager.addTorrent(torrent);
	}
}