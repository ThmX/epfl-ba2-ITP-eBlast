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

package eblast.test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import eblast.checksum.Hash;
import eblast.checksum.NullHashException;

public class HashTest {
    
    @Test
    public void testHash() throws NullHashException, UnsupportedEncodingException {
    	Hash hash1 = new Hash("cd4cc08c10070aeec0b0b2c5e40bc1c45111820d"); //
    	Hash hash2 = new Hash("cd4cc08c10070aeec0b0b2c5e40bc1c45111820d"); // 2 == 1
    	Hash hash3 = new Hash("cd4cc08c10070aefc0b0b2c5e40bc1c45119370d"); // 3 != 1
    	
    	assertEquals(hash1, hash2);
    	assertNotSame(hash1, hash3);
    }
    
    @Test(expected = NullHashException.class)
    public void testHashException() throws NullHashException, UnsupportedEncodingException {
    	byte[] nullBytes = null;
		new Hash(nullBytes);
    }
    
    @SuppressWarnings("unused")
	private void printHash(Hash hash) {
    	System.out.println("-----------------------------------------");
    	
		System.out.println("-----------------------------------------");
    }
}