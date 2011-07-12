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

package eblast.log;

/**
 * This Listener allows us to write the logs into the System standard Output Stream.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 01.03.2011 - Initial version
 * @version 1.1 - 22.05.2011 - Put it into a real class
 */
public class ConsoleLogger implements LogListener {

	/**
	 * Prints the logs using the following syntax:<br>
	 * <pre> [StreamName] Tag: Message</pre>
	 */
	public void logIt(String streamName, String tag, String msg) {
		System.out.println("[" + streamName + "] " + tag + ": " + msg);
	}

}
