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

package eblast.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * This class is here to count every byte that's been received.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 18.05.2011 - Initial version
 */
public class CounterInputStream extends InputStream implements ICounterStream {
	
	private InputStream in;
	
	private long mTotalBytesRead;
	private long mReadBytes;
	private long mLastTime;
	
	/**
	 * Default constructor. Creates and initializes the counter.
	 * @param is InputStream of the connection to count
	 */
	public CounterInputStream(InputStream is) {
		
		in = is;
		resetCounter();
	}
	
	/**
	 * Add the number of read byte, only if it is greater than 0.
	 * @param read number of bytes read
	 */
	private int addRead(int read) {
		
		if (read > 0) mReadBytes += read;
		return read;
	}

	/**
	 * @see java.io.FilterInputStream
	 */
	public int read() throws IOException {
		int value = in.read();
		if (value > 0) mReadBytes++;
		return value;
	}
	
	/**
	 * @see java.io.FilterInputStream
	 */
	public int read(byte[] b) throws IOException {
		return read(b, 0 , b.length);
    }
    
    /**
	 * @see java.io.FilterInputStream
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return addRead(in.read(b,off,len));
    }
	
	/**
	 * @see java.io.FilterInputStream
	 */
	public int available() throws IOException {
		return in.available();
	}
	
	/**
	 * Reinitializes the counters.
	 */
	public void resetCounter() {
		mTotalBytesRead = 0;
		mReadBytes = 0;
		mLastTime = new Date().getTime();
	}
	

	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the number of byte that has been read.
	 * @return number of byte that has been read.
	 */
	public long getTotalBytesRead() {
		return mTotalBytesRead;
	}
	
	/**
	 * Returns the average speed in byte per seconds.
	 * @return speed in byte per seconds.
	 */
	public double getAverageSpeed() {
		
		long now = new Date().getTime();
		
		double speed = (double)mReadBytes * 1000 / (now - mLastTime); // Compute the speed.
		
		resetCounter();
		return speed;
	}
}
