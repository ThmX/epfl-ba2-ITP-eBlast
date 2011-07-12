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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * This class is there to count every byte that's been sent. 
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 18.05.2011 - Initial version
 */
public class CounterOutputStream extends FilterOutputStream implements ICounterStream {
	
	private long mTotalWritten;
	private long mWritten;
	private long mLastTime;

	/**
	 * Create and initialize the counter
	 * @param is OutputStream of the connecion to count
	 */
	public CounterOutputStream(OutputStream os) {
		
		super(os);
		resetCounter();
	}
	
	/**
	 * Add the number of written bytes, only if it's greater than 0
	 * @param writtenBytes number of byte written
	 */
	private void addWrittenBytes(int writtenBytes) {
		
		if (writtenBytes > 0) {
			mWritten += writtenBytes;
		}
	}
	
	/**
	 * @see java.io.FilterOutputStream
	 */
	public void write(int b) throws IOException {
		write(new byte[]{(byte)b}, 0, 1);
	}
	
	/**
	 * @see java.io.FilterOutputStream
	 */
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
    }
    
	/**
	 * @see java.io.FilterOutputStream
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b,off,len);
		addWrittenBytes(len);
    }
	
	/**
	 * Reinitializes the counter
	 */
	public void resetCounter() {
		mTotalWritten = 0;
		mWritten = 0;
		mLastTime = new Date().getTime();
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Returns the number of byte that has been written into this Stream
	 * @return The number of byte that has been written into this Stream
	 */
	public long getTotalBytesRead() {
		return mTotalWritten;
	}
	
	/**
	 * Returns the average speed in byte per seconds
	 * @return The speed in byte per seconds
	 */
	public double getAverageSpeed() {
		long now = new Date().getTime();
		
		double speed = (double)mWritten * 1000 / (now - mLastTime); // get average speed.
		
		resetCounter();
		
		return speed;
	}

}
