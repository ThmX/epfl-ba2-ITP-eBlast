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

package eblast.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Allows the user to create a Keypair object (RSA or XOR).
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 22.02.2011 - Initial version
 * @version 1.1 - 27.02.2011 - Add XORKeyPair Generator
 * @version 1.2 - 20.05.2011 - Fix mod length
 */
public final class KeyGenerator {
	
	/**
	 * Create a KeyPair containing a private key (N bits)
	 * @param bitsLength The bits count of the private key.
	 * @return Generated RSA KeyPair
	 */
	public static RSAKeyPair generateRSAKeyPair(int bitsLength) {
		
		// Local variables. Will be erased in the end of this method.
		BigInteger p;
		BigInteger q;
		BigInteger phi;
		BigInteger mod;
		BigInteger publicKey = RSAKeyPair.BI_TWO.pow(1<<4).add(RSAKeyPair.BI_ONE); // 2^16 + 1
		BigInteger privateKey = new BigInteger("0");
		
		// Variables used for creating the private RSA key.
		do{
			p = generatePrimeNumber(bitsLength/2);
			do {
				q = generatePrimeNumber(bitsLength/2);
			} while(p.equals(q)); // p has to be different from q.
			
			// Computing mod and phi.
			mod=p.multiply(q);
			phi=p.subtract(RSAKeyPair.BI_ONE).multiply(q.subtract(RSAKeyPair.BI_ONE));
		
		} while (!publicKey.gcd(phi).equals(RSAKeyPair.BI_ONE)); //the public key and phi have to be relatively prime with each other.
		
		// Create the private key (which is the inverse of the public key).
		privateKey = publicKey.modInverse(phi);
		
		return new RSAKeyPair(mod, publicKey, privateKey, bitsLength);
	}
	
	/**
	 * Create a KeyPair containing a key (N bits)
	 * @param bitsLength The bits count of the private key.
	 * @return Generated XOR KeyPair
	 * @throws KeyLengthException 
	 */
	public static XORKeyPair generateXORKeyPair(int bitsLength) {
		
		byte[] key = new byte[bitsLength];
		SecureRandom keygen = new SecureRandom();
		keygen.nextBytes(key); // Creates the key.
		
		return new XORKeyPair(key);
	}
	
	/**
	 * Return a prime number of N bits.
	 * @param bitsLength length of the desired prime number.
	 * @return a prime number.
	 */
	private static BigInteger generatePrimeNumber(int bitsLength) {
		return BigInteger.probablePrime(bitsLength, new SecureRandom());
	}
}
