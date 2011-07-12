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

/**
 * This interface is used by the Visitor design pattern.
 * It can be seen as an algorithm database, where <code>visit()</code>
 * is an access to the algorithm
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 26.05.2011 - Initial version
 */
public interface MessageVisitor {

	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(BitField bf);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(Choke c);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(Have h);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(Interested i);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(NotInterested notI);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(Request r);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(SendBlock sb);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(Unchoke un);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(SendRSAKey rsa);
	
	/**
	 * Executes the algorithm related to the message given in parameter.
	 * @param bf Message that calls its Message-specific algorithm
	 */
	public void visit(SendSymmetricKey sym);
}
