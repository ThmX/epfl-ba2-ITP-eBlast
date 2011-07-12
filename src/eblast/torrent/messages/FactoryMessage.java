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

package eblast.torrent.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import eblast.Convertor;
import eblast.log.Log;
import eblast.torrent.messages.Message.ID;

/**
 * Creates an instance of a Message according to the data's contained in the Stream given in parameters.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - Initial version.
 */
public class FactoryMessage {

	// Hides the constructor
	private FactoryMessage() {}
	
	/**
	 * Static class used to create a Message from an InputStream
	 * @param in InputStream from where to read the data
	 * @return a Message
	 * @throws IOException
	 * @throws MessageException
	 */
	public static Message createMessage(DataInputStream in) throws IOException, MessageException {
		
		Message returnMessage = null;
		
		int length = 0;
		ID id = null;
		byte[] payload = null;
		
		// Read the length
		byte[] lenBytes = new byte[4];

		in.readFully(lenBytes);
		length = Convertor.toInteger(lenBytes);
		
		if (length == 0) {
			
			Log.d("FactoryMessage", "KeepAlive has been received."); // Debug
			return null;
		}
		
		// Read the ID
		try {
			id = ID.values()[in.readByte()]; // Get the id value in terms of the ID enum.
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MessageException(id, "This ID isn't implemented.");
		}
	
		Log.d("FactoryMessage", "Message(" + id.name() + ") has been received."); // Debug
		
		// Creates the right Message according to the id.
		switch (id) {
		 
		case choke :
			returnMessage = new Choke();
			break;
			
		case unchoke :
			returnMessage = new Unchoke();
			break;
			
		case interested :
			returnMessage = new Interested(); 
			break;
			
		case notInterested :
			returnMessage = new NotInterested(); 
			break;
			
		case have :
			if (length == Have.DEFAULT_LENGTH) {
				returnMessage = new Have(in.readInt());
			}
			break;
			
		case bitfield :
			payload = new byte[length - BitField.DEFAULT_LENGTH];
			in.readFully(payload);
			returnMessage = new BitField(payload);
			break;
			
		case request :
			if (length == Request.DEFAULT_LENGTH) {
				int index 		= in.readInt();
				int begin 		= in.readInt();
				int tmpLength 	= in.readInt();
				
				returnMessage = new Request(index, begin, tmpLength);
			} else {
				throw new MessageException(id, "Wrong length");
			}
			break;
		
		case piece :
			
			int payloadIndex 	= 0;
			int payloadBegin 	= 0;
			
			payloadIndex = in.readInt();
			payloadBegin = in.readInt();
			
			payload = new byte[length - SendBlock.DEFAULT_LENGTH];
			in.readFully(payload);
		
			returnMessage = new SendBlock(payloadIndex, payloadBegin, payload);
			break;
			
		case sendRSAKey :
			
			int keyLength = in.readInt();
			
			// Fetch the public key. 
			int publicKL = in.readInt();
			byte[] publicKey = new byte[publicKL];
			in.readFully(publicKey);
			
			// Fetch the modulo.
			int moduloLength = in.readInt();
			byte[] modulo = new byte[moduloLength];
			in.readFully(modulo);
			
			returnMessage = new SendRSAKey(
					keyLength,						// Bit Count
					new BigInteger(publicKey),		// Public Key
					new BigInteger(modulo)			// Modulo
				);
			
			break;
		
		case sendSymmetricKey :
			
			// Fetch the key
			byte[] key = new byte[length - SendSymmetricKey.DEFAULT_LENGTH];
			in.readFully(key);
			
			Log.d("FactoryMessage SymmetricKey", Arrays.toString(key));
			
			returnMessage = new SendSymmetricKey(key);
			
			break;
			
		default :
			throw new MessageException(id, "This ID isn't implemented.");
		}
		
		return returnMessage;
	}
}