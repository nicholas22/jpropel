// /////////////////////////////////////////////////////////
// This file is part of Propel.
//
// Propel is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Propel is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with Propel. If not, see <http://www.gnu.org/licenses/>.
// /////////////////////////////////////////////////////////
// Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
// /////////////////////////////////////////////////////////
package propel.core.security.hashing;

/**
 * Represents the interface which all hash implementations implement
 */
public abstract class HashAlgorithm
{
  /**
   * Computes the hash value for the specified byte array.
   */
  /*
   * public byte[] computeHash(byte[] data){
   * 
   * }
   */

  /**
   * Computes the hash value for the specified Stream object.
   */
  /*
   * public byte[] computeHash(InputStream is){
   * 
   * }
   */

  /**
   * Computes the hash value for the specified byte array.
   */
  /*
   * public byte[] computeHash(byte[] data, int offset, int length){
   * 
   * }
   */

  /**
   * When overridden in a derived class, routes data written to the object into the hash algorithm for computing the hash.
   */
  // protected abstract void hashCore(byte[] data, int start, int size);

  /**
   * When overridden in a derived class, finalizes the hash computation after the last data is processed by the cryptographic stream object.
   */
  // protected abstract byte[] hashFinal();
}
