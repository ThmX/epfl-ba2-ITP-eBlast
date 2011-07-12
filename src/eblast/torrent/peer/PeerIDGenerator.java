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

package eblast.torrent.peer;

import java.util.Random;

/**
 * This class generates a random peerID.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 0.1 - 07.03.2011 - Initial version
 */
public class PeerIDGenerator {

	/**
	 * Main method of this class
	 * @return the generated peer ID
	 */
	public static String generateID() {
		
		StringBuilder sb = new StringBuilder();
		
		// we used to usurpate uTorrent (UT) but it seems that it doesn't like that, and
		// even detects us as a FAKE. (Fail.)
		// So we're trying Azureus (AZ)
		//sb.append("-XY2456-");
		sb.append("-eB0100-"); // eBlast 0.1
		
		Random rand = new Random();
		for(int i = 0; i < 12; i++) {
			sb.append(rand.nextInt(10));
		}
		
		return sb.toString();
	}
	
}
