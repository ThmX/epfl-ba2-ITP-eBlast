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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.List;

import eblast.checksum.Hash;
import eblast.checksum.NullHashException;
import eblast.log.Log;

/**
 * This Class represents a File contained into the MetaInfo.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 */
public class TorrentFile {
	
	private long mLength;
	private Hash mMD5Sum;
	private String mPath;
	private File mFile;
	private RandomAccessFile mRAF = null;
	
	/**
	 * Create a File with informations from the MetaInfo.
	 * @param length length of the file (in bytes)
	 * @param path list directories to reach the file, the last one is the file itself
	 * @param md5sum (Optional) MD5 Checksum of the File : null by default
	 * @throws UnsupportedEncodingException If the system doesn't support UTF-8
	 */
	public TorrentFile(int length, List<String> path, String md5sum) throws UnsupportedEncodingException {
		
		mLength	= length;
		mPath = listPathToString(path);
		mFile = null;
		
		try {
			mMD5Sum	= new Hash(md5sum);
			
		} catch (NullHashException e) {
			
			// If there is a problem with the MD5 Hash, it just will be ignored.
			mMD5Sum = null;
		}
	}
	
	/**
	 * Open the file from the directory given in parameter.
	 * 
	 * The current file will be contained in "<code>dir</code>/<code>mPath</code>", 
	 * where <code>dir</code> is the path to the main folder and <code>mPath</code> is the relative
	 * path of the TorrentFile into the Torrent.
	 * @param dir directory where the file will be downloaded to
	 */
	public void open(File dir) throws FileNotFoundException {
		mFile = new File(dir.getAbsolutePath() + System.getProperty("file.separator") + mPath);
		
		Log.d("File", "opening " + mFile.getAbsolutePath()); // Debug
		mRAF = new RandomAccessFile(mFile, "rw");
	}
	
	/**
	 * Closes the file.
	 */
	public void close() {
		try {
			mRAF.close();
		} catch (IOException e) {
			Log.e("TorrentFile::close() ==> IOException", e.getMessage());
		} finally {
			mRAF = null;
		}
	}
	
	/**
	 * Erase the file.
	 */
	public void erase() {
		close();
		mFile.delete();
	}

	/**
	 * Converts a String List that represents a path in an usual String Path<br>
	 * Example:
	 * <ul>
	 *   <li> dir1
	 *   <li> dir2
	 *   <li> file.txt
	 * </ul>
	 * This is what the method would return in this example:<br>
	 * <ul>
	 *   <li> <b>Unix System:</b> <i>dir1/dir2/file.txt</i><br>
	 *   <li> <b>Windows System:</b> <i>dir1\dir2\file.txt</i>
	 * </ul>
	 * <hr>
	 * @param list list of each directories plus the filename<br>
	 * @return string path of the list
	 */
	private static String listPathToString(List<String> list) {
		
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(java.io.File.separator + s);
		}
		
		builder.deleteCharAt(0); // Remove the first File separator (Relative path)
		return builder.toString();
	}
	
	/************************ GETTERS / SETTERS *************************/
	
	/**
	 * Length of the file in byte.
	 * @return Integer representing the size in byte.
	 */
	public long length() {
		return mLength;
	}
	
	/**
	 * Path of the file.
	 * @return String representing the path to reach the file.
	 */
	public String getPath() {
		return mPath;
	}
	
	/**
	 * MD5 Hash of the file.
	 * @return The Hash representing the MD5, null if doesn't used.
	 */
	public Hash getMD5Sum() {
		return mMD5Sum;
	}
	
	/**
	 * returns a randomAccessFile to read/write in the file.
	 * @return randomAccessFile to read/write in the file.
	 */
	public RandomAccessFile getRandomAccessFile() {
		return mRAF;
	}
}