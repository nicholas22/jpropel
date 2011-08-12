/*
 ///////////////////////////////////////////////////////////
 //  This file is part of Propel.
 //
 //  Propel is free software: you can redistribute it and/or modify
 //  it under the terms of the GNU Lesser General Public License as published by
 //  the Free Software Foundation, either version 3 of the License, or
 //  (at your option) any later version.
 //
 //  Propel is distributed in the hope that it will be useful,
 //  but WITHOUT ANY WARRANTY; without even the implied warranty of
 //  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 //  GNU Lesser General Public License for more details.
 //
 //  You should have received a copy of the GNU Lesser General Public License
 //  along with Propel.  If not, see <http://www.gnu.org/licenses/>.
 ///////////////////////////////////////////////////////////
 //  Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
 ///////////////////////////////////////////////////////////
 */
package propel.core.security.cryptography.ciphers;

/**
 * The interface that ciphers adhere to
 */
public interface ICipher
{
	/**
	 * Return the block size of the block cipher
	 */
	int getBlockSize();

	/**
	 * Returns the supported key sizes of the cipher
	 */
	int[] getKeySizes();

	/**
    * Encrypts the provided in data from the in start position,
    * puts it in the out data array starting at the out position.
    * Does this for a specified byte count, which must be a multiple of the cipher's block size.
    *
    * @throws NullPointerException    An argument is null
    * @throws IndexOutOfBoundsException An index is out of range
    * @throws IllegalArgumentException  An argument is out of range, or the output array is not large enough
    * @throws RuntimeException An unexpected error has occurred
	 */
	void encrypt(byte[] dataIn, int posIn, byte[] dataOut, int posOut, int count);

	/**
    * Decrypts the provided in data from the in start position,
    * puts it in the out data array starting at the out position.
    * Does this for a specified byte count, which must be a multiple of the cipher's block size.
    *
    * @throws NullPointerException    An argument is null
    * @throws IndexOutOfBoundsException An index is out of range
    * @throws IllegalArgumentException  An argument is out of range, or the output array is not large enough
    * @throws RuntimeException An unexpected error has occurred
	 */
	void decrypt(byte[] dataIn, int posIn, byte[] dataOut, int posOut, int count);
}
