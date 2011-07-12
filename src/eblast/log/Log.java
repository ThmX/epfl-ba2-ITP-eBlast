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

import java.util.LinkedList;
import java.util.List;

/**
 * This class spreads log messages to all the classes that have the <code>LogListener</code>
 * interface implemented. By default, it will write into the System standard output Stream.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 01.03.2011 - Initial version
 */
public final class Log {

	private static boolean mDebugMode = false; 

	private static List<LogListener> mListeners = new LinkedList<LogListener>(); // List of all the LogListeners.
	
	/**
	 * Add a listener to the Stack.
	 * @param listener Listener to add.
	 */
	public static void addListener(LogListener listener) {
		mListeners.add(listener);
	}
	
	/**
	 * Remove the Listener from the Stack.
	 * @param listener Listener to remove.
	 */
	public static void removeListener(LogListener listener) {
		mListeners.remove(listener);
	}
	
	/**
	 * Spread a log message to all the LogListeners registered to this class.
	 * @param streamName name of the Stream : it is either Information, Debug or Error.
	 * @param tag tag of the message.
	 * @param msg message itself.
	 */
	private static void notify(String streamName, String tag, String msg) {
		for (LogListener listener: mListeners) {
			listener.logIt(streamName, tag, msg);			
		}
	}
	
	/**
	 * Spread an Information Message.
	 * @param tag used to personnalize the information message (location/type of message, etc).
	 * @param msg Message to be displayed.
	 */
	public static void i(String tag, String msg) {
		notify("Information", tag, msg);
	}

	/**
	 * Spread a Debug Message.This method is
	 * activated only if the debug mode is on.
	 * @param tag used to personnalize the information message (location/type of message, etc).
	 * @param msg Message to be displayed.
	 */
	public static void d(String tag, String msg) {
		if (mDebugMode) {
			notify("Debug", tag, msg);
		}
	}
	
	/**
	 * Spread an Error Message.
	 * @param tag used to personnalize the error message (location/type of error, etc).
	 * @param msg Message to be displayed.
	 */
	public static void e(String tag, String msg) {
		notify("Error", tag, msg);
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Activate/desactivate the debug mode with the <code>active</code> boolean in parameter.
	 * @param active activate the Debug Mode Display if true, desactivate it otherwise.
	 */
	public static void setDebugMode(boolean active) {
		Log.mDebugMode = active;
	}
}
