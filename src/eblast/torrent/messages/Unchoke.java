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
 * This class represents the Unchoke message, according to the Bittorent protocol.
 *
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 12.03.2011 - Initial version
 */
public class Unchoke extends Message {

	/**
	 * Default constructor.
	 */
	public Unchoke() {
		
		super(DEFAULT_LENGTH, ID.unchoke);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(MessageVisitor v) {
		v.visit(this);
	}
}