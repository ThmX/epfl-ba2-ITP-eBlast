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

package eblast.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XML Parser used to extract all the settings of this program
 * from an XML file.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 */
public class XMLSettings {
	
	// XML Header
	private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	private EBlastSettings mSettings;
	private File mXMLFile;
	

	/**
	 * Default constructor.
	 * @param settings object where we are going to store the informations extracted from the xml file
	 * @param xmlfile xml file to parse
	 */
	public XMLSettings(EBlastSettings settings, File xmlfile) {
		mSettings = settings;
		mXMLFile = xmlfile;
	}

	/**
	 * Transforms all the data contained into EBlastSettings into an
	 * XML format (it actually writes into the xml file given in parameter in the constructor).
	 * @throws XMLException
	 */
	public void toXML() throws XMLException {
		try {
			
			// We create the arborescence if it doesn't exist.
			if (!mXMLFile.getParentFile().exists()) {
				mXMLFile.getParentFile().mkdirs();
			}
			
			// Line separator (Platform-specific)
			String endl = System.getProperty("line.separator");
			
			FileWriter fw = new FileWriter(mXMLFile);
	
			fw.write(XML_VERSION + endl);
			fw.write(mSettings.toXML());
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			System.out.println("Error writing");
			throw new XMLException("Error when writing into the xml file: " + mXMLFile.getAbsolutePath());
		}
	}

	/**
	 * Parsing function : XML ==> Program Settings
	 * @throws XMLException
	 */
	public void fromXML() throws XMLException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			
			XMLSettingsHandler handler = new XMLSettingsHandler(mSettings);
			
			XMLReader xmlreader = sp.getXMLReader();
	
			xmlreader.setContentHandler(handler);
			xmlreader.parse(new InputSource(new FileInputStream(mXMLFile)));
			
		} catch (SAXException e) {
			System.out.println("Error SAX");
			throw new XMLException("Error XML with SAX");

		} catch (ParserConfigurationException e) {
			System.out.println("Error Parser");
			throw new XMLException("Error XML with the parser configurations");
				
		} catch (FileNotFoundException e) {
			System.out.println("Error FNF");
			throw new XMLException("File not found");
			
		} catch (IOException e) {
			System.out.println("Error IO");
			throw new XMLException("Error Input/Output");
		}
	}
}