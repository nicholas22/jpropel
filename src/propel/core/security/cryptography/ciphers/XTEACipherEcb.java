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
package propel.core.security.cryptography.ciphers;

import java.security.InvalidKeyException;

/**
 * XTEA ECB implementation
 */
public class XTEACipherEcb
    extends AbstractCipherEcb
{

  /**
   * The block size of the cipher
   */
  public static final int BLOCK_SIZE = 8;
  /**
   * If a cipher supports multiple key sizes, this value stores a suggested value to use
   */
  public static final int RECOMMENDED_KEY_SIZE = 16;
  /**
   * Supported key sizes
   */
  private static final int[] KEY_SIZES = {RECOMMENDED_KEY_SIZE};

  // cipher
  private final XTEA xtea;

  /**
   * Initializes with the secret key
   * 
   * @throws NullPointerException An argument is null
   * @throws InvalidKeyException Key length is not supported by this cipher
   */
  public XTEACipherEcb(byte[] key)
      throws InvalidKeyException
  {
    super(key);

    xtea = new XTEA();
    xtea.setKey(key);
  }

  /**
   * {@inheritDoc}
   */
  public int getBlockSize()
  {
    return BLOCK_SIZE;
  }

  /**
   * {@inheritDoc}
   */
  public int[] getKeySizes()
  {
    return KEY_SIZES;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getRecommendedKeySize()
  {
    return RECOMMENDED_KEY_SIZE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void encrypt(byte[] dataIn, byte[] dataOut, int offset, int count)
  {
    checkArguments(dataIn, dataOut, offset, count);

    xtea.encrypt(dataIn, dataOut, offset, count);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void decrypt(byte[] dataIn, byte[] dataOut, int offset, int count)
  {
    checkArguments(dataIn, dataOut, offset, count);

    xtea.decrypt(dataIn, dataOut, offset, count);
  }

}
