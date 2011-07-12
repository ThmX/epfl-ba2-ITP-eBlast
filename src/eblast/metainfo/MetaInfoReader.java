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

package eblast.metainfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import eblast.bencoding.BDecoder;
import eblast.bencoding.BEDictionary;
import eblast.checksum.Hash;
import eblast.http.HTTPGet;

/**
 * This class allows the instantiation of a MetaInfo from
 * a Local File or Web File.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 27.02.2011 - Initial version
 * @version 1.1 - 04.03.2011 - Modification because of the need of info_hash
 */
public class MetaInfoReader {
	/**
	 * Allows to create an Instance of a MetaInfo from a local file.
	 * @param filename filepath of the MetaInfo File.
	 * @return An instance of the Parsed MetaInfo.
	 * @throws MetaInfoException If an error has occurred and the MetaInfo is unusable. 
	 * @throws FileNotFoundException If the filepath reaches a non-existing file.
	 * @throws IOException If the file is not readable or an error has occurred to read it.
	 */
	public static MetaInfo openMetaInfo(String filename) throws MetaInfoException, FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(filename);
		BDecoder decoder = new BDecoder(fis);
		decoder.setSpecialMapName(MetaInfo.KEY_INFO);
		BEDictionary dict = new BEDictionary(decoder.bdecodeMap());
		
		return new MetaInfo(dict, new Hash(decoder.getSpecialMapDigest()));
	}
	
	/**
	 * Allows to create an Instance of a MetaInfo from a web link.
	 * @param urlFile URL of the MetaInfo File.
	 * @return An Instance of the MetaInfo.
	 * @throws MetaInfoException If an error has occurred and the MetaInfo is unusable.
	 * @throws MalformedURLException If the url is unusable.
	 * @throws IOException If the file is not readable or an error has occurred to read it.
	 */
	public static MetaInfo openMetaInfoFromURL(String urlFile) throws MetaInfoException, MalformedURLException, IOException {
		File dir = new File(System.getProperty("user.home") + File.separatorChar + ".eBlast");
		File file = HTTPGet.downloadFileTo(urlFile, ".torrent", dir);
		return openMetaInfo(file.getAbsolutePath());
	}
}