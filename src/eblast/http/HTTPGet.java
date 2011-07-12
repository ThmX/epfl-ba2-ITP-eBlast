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

package eblast.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.util.Map;

import eblast.log.Log;

/**
 * This class allows the user to perform a HTTP GET Request.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 * @version 1.1 - 04.03.2011 - Doesn't use BEValue anymore.
 */
public final class HTTPGet {
	
	/**
	 * This method is there to concatenate the map values and keys,<br>
	 * the following example shows how the concatenation is done:<br>
	 * <ul>
	 *   <li> key1: value1
	 *   <li> key2: value2
	 *   <li> key3: value3
	 * </ul>
	 * This is what the method would return with this map:
	 * <pre>	key1=value1&key2=value2</pre>
	 * 
	 * @param map map of Entry&#060;String key, String value&#062;
	 * @return UTF-8 String of the entries URL Encoded. 
	 * @throws UnsupportedEncodingException if the UTF-8 is not supported on this system.
	 */
	private static String mapToURLString(Map<String,String> map) throws UnsupportedEncodingException {
		
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) { // we concatenate each key in the Map.
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append("&");
		}
		
		builder.deleteCharAt(builder.length()-1); // Remove the last '&'
		return builder.toString();
	}
	
	/**
	 * This method is used to open an InputStream from an URL.
	 * 
	 * @param urlString url of the stream to be opened
	 * @param gets map of Entry&#060;String key, String value&#062;, null if unused
	 * @return InputStream of the URL
	 * 
	 * @throws IOException if an I/O problem occured.
	 */
	public static InputStream openURLStream(String urlString, Map<String,String> gets) throws IOException {
		
		if ((gets != null) && !gets.isEmpty()) // If the gets list is not null and not empty
			urlString += "?" + mapToURLString(gets);
		
		Log.d("HTTPGet", urlString); // Debug
		
		// Opens an InputStream from an URL.
		URL url = new URL(urlString); URLConnection urlConn = url.openConnection();
		urlConn.setReadTimeout(5000); // If after 5 seconds we still cannot read, we close the stream.
		
		return urlConn.getInputStream();
	}
	
	/**
	 * This method is used to download a file from an URL into the directory <code>dir</code>
	 * it will create a file with the prefix <code>web</code> and with the extension <code>ext</code>.
	 * 
	 * @param urlString url of the stream to be opened.
	 * @param gets A map of Entry&#060;String key, String value&#062;, null if unused
	 * @return InputStream of the URL
	 * 
	 * @throws IOException An Input/Output Exception has occured
	 */
	public static File downloadFileTo(String urlString, String ext, File dir) throws IOException {
		
		InputStream in		= null;
		OutputStream out	= null;
		File file			= null;
		
		if (!dir.exists()) { // If the download directory does not exist, we create it.
			dir.mkdirs();
		}
		
		try {
			in = openURLStream(urlString, null);
			file = File.createTempFile("web", ext, dir); // Creates a new empty file.
				
			out = new FileOutputStream(file);

			byte[] buffer = new byte[2048];
			int length;

			// Copy every input into the output.
			while ((length = in.read(buffer, 0, 2048)) > 0) {
				out.write(buffer, 0, length);
			}
			
			Log.d("HTTPGet", "Download " + urlString + " to " + file.getAbsolutePath()); // Debug
			
		/*
		 * This catch/finally is because we need to log what happened in this class and because
		 * we need to close all streams (if we had thrown an exception without catching it, 
		 * we wouldn't have been able to close the streams properly).
		 */
		} catch (IOException e) { // Allows to close Streams
			
			Log.e("HTTPGet", "Error while downloading " + urlString + " to "
					+ file.getAbsolutePath() + "\n\t" + e.getMessage());
			
			throw e;
			
		} finally { // Closes all Streams
			
			if (in != null) in.close();
			if (out != null) out.close(); 
		}
		
		return file;
	}
}
