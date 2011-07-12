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

package eblast.exceptions;

import eblast.log.Log;

/**
 * This Exception Class is there to handle all Exceptions of the project.<br>
 * This is the list of what it is currently doing :
 * <ul>
 * 		<li> Logging Exception : To log a custom exception, we only need to call super() (instead of doing a Log.e())
 * 		<li> ...
 * </ul> 
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 28.02.2011 - Initial version
 */
public class EBlastException extends Exception {
	/**
	 * Auto-generated SerialVersionUID
	 */
	private static final long serialVersionUID = -2625073152566779275L;

	/**
	 * Default constructor.
	 * @param msg message to be displayed.
	 */
	public EBlastException(String msg) {
		
		super(msg);
		Log.e("Exception", msg);
	}
}
