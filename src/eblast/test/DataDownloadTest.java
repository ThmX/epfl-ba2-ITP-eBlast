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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.After;

import eblast.test.Download;
import eblast.bencoding.InvalidBEncodingException;


public class DataDownloadTest {

	boolean finished = false;
	boolean success = false;

	Process trackerProcess = null;
	Process clientProcess = null;

	@Test
	public void testDataExchange() throws InterruptedException, InvalidBEncodingException, IOException {
		
		// Start the tracker
		//trackerProcess = jarStarter("trunk/third_party/tracker.jar");
		//checkTrackerState();
		
		// Start uploading
		//clientProcess = jarStarter("trunk/third_party/client.jar");
		
		// Start downloading
		new Thread() {
			public void run() {
				try {
					Download download = new Download();
					download.run((new Random()).nextInt(30000) + 6881, "trunk/data/LePetitPrince.torrent", true, true);
					while (true) {
						if (download.torrent.isComplete()) {
							success = true;
							finished = true;
						}
						Thread.sleep(100L);
					}
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}.start();
		
		while (!finished) {
			Thread.sleep(100L);
		}
		
		Assert.assertTrue(success);
	}

	@After
	public void destroyProcesses() {
		System.out.println("Exiting...");
		
		if (clientProcess != null) clientProcess.destroy();
		if (trackerProcess != null) trackerProcess.destroy();
	}
	
	public static Process jarStarter(String path) throws IOException {
		long trackerStart = System.nanoTime();
		Process p = Runtime.getRuntime().exec(new String [] {"java", "-jar", path});		
		
		long trackerStarted = System.nanoTime();
		System.out.println(path + " started in " + (((trackerStarted - trackerStart) / 10000) / 100.0) + " ms");

		return p;
	}

	public void checkTrackerState() throws InterruptedException {
		Socket testSocket = null;
		while (testSocket == null) {
			try {
				testSocket = new Socket(InetAddress.getLocalHost(), 6969);
			} catch (Exception e) {
				Thread.sleep(100L);
			}
		}
		assertTrue("Tracker started", testSocket.isBound());
	}
}