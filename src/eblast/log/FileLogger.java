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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * This Listener allows us to write the logs into a file.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.05.2011 - Initial version
 */
public class FileLogger implements LogListener {
	
	private PrintWriter mPrintWriter;
	
	/**
	 * Initialize a File logger.
	 * @param filePath Log file were all messages will be written.
	 */
	public FileLogger(String filePath) throws FileNotFoundException {
		mPrintWriter = new PrintWriter(new File(filePath));
	}
	
	/**
	 * Prints the logs using the following syntax:<br>
	 * <pre> [StreamName] Tag: Message</pre>
	 */
	public void logIt(String streamName, String tag, String msg) {
		mPrintWriter.println("[" + streamName + "] " + tag + ": " + msg);
		mPrintWriter.flush();
	}

}
