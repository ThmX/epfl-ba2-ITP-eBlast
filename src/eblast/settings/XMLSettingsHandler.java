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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class evaluates the XML code parsed by the XMLSettings class
 * and "execute it", for example on an XML like this one :
 * <pre>
 * 	<?xml version="1.0" encoding="UTF-8"?>
	<eblast port="22701" maxpeers="60">
		<download>/home/users/Downloads</download>
	</eblast>
 * </pre>
 * this class would take the "port" tag and assign it to
 * the attribute mPort in the class EBlastSettings, etc.
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 25.05.2011 - Initial version
 * @see org.xml.sax.helpers.DefaultHandler
 */ 
public class XMLSettingsHandler extends DefaultHandler {

	private EBlastSettings mSettings;
	private StringBuilder mBufferBuilder;

	/**
	 * Default constructor
	 * @param settings Object that contains all the parameters of this program
	 */
	public XMLSettingsHandler(EBlastSettings settings) {
		super();
		mSettings = settings;
		mBufferBuilder = new StringBuilder();
	}

	/**
	 * Receive notification of character data inside an element.
	 * @param ch The characters.
	 * @param start The start position in the character array.
	 * @param length The number of characters to use from the character array.
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void characters(char[] ch, int start, int length) {
		mBufferBuilder.append(ch, start, length);
	}

	/**
	 * Receive notification of the start of an element.
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @param attributes The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		String value;
		if (qName.equals("eblast")) {
			if ((value = attributes.getValue("port")) != null) mSettings.setPort(Integer.valueOf(value));
			if ((value = attributes.getValue("maxpeers")) != null) mSettings.setMaxPeers(Integer.valueOf(value));
			if ((value = attributes.getValue("encrypted")) != null) mSettings.setEncryption(Boolean.valueOf(value));
			if ((value = attributes.getValue("ignoreunencrypted")) != null) mSettings.setIgnoreUnencrypted(Boolean.valueOf(value));
		} else if (qName.equals("download")) {
			if ((value = attributes.getValue("path")) != null) mSettings.setDownloadDir(new File(value));
		}
	}
	
	/**
	 * Receive notification of the end of an element.
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String qName) {
		// Nothing to do (for now)
	}
}
