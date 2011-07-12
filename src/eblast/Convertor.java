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

package eblast;

/**
 * This utility class allows the user to convert hexadecimal values into decimal values. 
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 08.03.2011 - Initial version
 */
public final class Convertor {
	
	// The Convertor class is not instanciable.
	private Convertor() {}
	
	/**
	 * Convert an array of Bytes into an Hexadecimal String.
	 * Each byte is converted into its hexadecimal value and concatenated along with the others.
	 * @param bytes array of Bytes that is going to be converted.
	 * @return Hexadecimal String of the Checksum.
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder strBuilder = new StringBuilder();
		
		for (byte b: bytes) {
			// Trick to convert byte to int and MASK the useless bits
			String hex = Integer.toHexString(b & 0xFF);
			
			// Concatenate a '0' if the number is between 0 and 9.
			if (hex.length() == 1) strBuilder.append("0");
			
			strBuilder.append(hex);
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * Convert an Hexadecimal String into a byte array.
	 * If the string has a odd number of elements, then it will concatenate a '0' in front of it.
	 * @param hexstr String representing an Hexadecimal String.
	 * @return array of bytes of the conversion of the Hexadecimal String.
	 */
	public static byte[] toBytes(String hexstr) {
		
		// Add a '0' in front of the String if it does not exist.
		if (hexstr.length() % 2 != 0)
			hexstr = "0" + hexstr;
		
		byte[] bytes = new byte[hexstr.length() / 2];
		
		for (int i=0; i<bytes.length; i++) {
			String hex = hexstr.substring(2 * i, 2 * i + 2); // Take a pair of char...
			bytes[i] = (byte) Integer.parseInt(hex, 16); // ...and convert them.
		}
		
		return bytes;
	}
	
	/**
	 * Convert an hexadecimal value into a decimal value.
	 * @param hexstr String representing the hexadecimal value.
	 * @return decimal value of the hexadecimal value.
	 */
	public static int toInteger(byte[] bytes) {
		
		int value = 0;
		for (byte b: bytes) {
			value <<= 8;			// Shift left all already added bytes.
			value |= 0xFF & b;		// Perform a OR-bitwise with the byte.
		}
		return value;
	}
	
	/**
	 * This method is there to format the bytes with the right unit (K, M, G, T, P, E).
	 * @param num number to be formatted
	 * @param dec number of decimal
	 * @param suffix suffix that will be added after the string
	 * @return the number formatted at the right number of decimal with the unit and the suffix
	 */
	public static String formatBytes(double num, int dec, String suffix) {
		
		final String[] UNIT_SYMBOL = {"B", "kiB", "MiB", "GiB", "TiB", "PiB", "EiB"};
		final int DIVIDER = 1024;
		int unitSymbolIdx = 0;

		double decimalDivisor = Math.pow(10, dec);
		
		while (num >= 1024) {
			
			if (unitSymbolIdx >= UNIT_SYMBOL.length) { // If the number to format is above the ExaByte (highly unlikely)
				break;
			}

			num /= DIVIDER;
			unitSymbolIdx++;
		}
		
		// return the number with its correct unit.
		return Double.toString((int)(decimalDivisor * num) / decimalDivisor) + " " + UNIT_SYMBOL[unitSymbolIdx] + suffix;
	}
}






