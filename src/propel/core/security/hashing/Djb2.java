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
 * 
 * TODO: implement
 */
public class Djb2
{
  /*
   * /// <summary> /// A Djb2 non-cryptographic hashing algorithm implementation /// </summary> public class Djb2 : HashAlgorithm { #region
   * Constants // resulting string hash size always has this size private const int HASH_SIZE = 10; // Int32.MaxValue.ToString().Length //
   * magic number of djb2 private const uint START = 5381; #endregion
   * 
   * #region Private /// <summary> /// This is used to convert the hash integer into a string byte array /// and formatting it onto the zero
   * block constant. /// </summary> private static readonly Encoding DEFAULT_ENCODING = Encoding.ASCII;
   * 
   * /// <summary> /// This stores the current hash /// </summary> private uint hash; #endregion
   * 
   * #region Public methods /// <summary> /// Perform any initialization here /// </summary> public override void Initialize() { hash =
   * START; } #endregion
   * 
   * #region Protected methods /// <summary> /// Compute the hash here and store it. /// We ignore ibStart and cbSize. /// </summary>
   * protected override void HashCore(byte[] data, int ibStart, int cbSize) { foreach(byte b in data) hash = ((hash << 5) + hash) + b; /*
   * hash * 33 + b
   */
  /*
   * }
   * 
   * /// <summary> /// Return the computed hash here /// </summary> protected override byte[] HashFinal() { StringBuilder sb = new
   * StringBuilder(HASH_SIZE); string hashStr = hash.ToString();
   * 
   * // result should have a specific size sb.Append(hashStr.PadLeft(HASH_SIZE, CONSTANT.ZERO_CHAR));
   * 
   * return DEFAULT_ENCODING.GetBytes(sb.ToString()); } #endregion }
   */
}
