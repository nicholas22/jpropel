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
import lombok.Validate;
import lombok.Validate.NotNull;
import propel.core.utils.ArrayUtils;
import propel.core.utils.Linq;

/**
 * Provides common functionality across all ciphers
 */
public abstract class AbstractCipherEcb
    implements ICipher
{
  /**
   * Initializes with the secret key
   * 
   * @throws NullPointerException An argument is null
   * @throws InvalidKeyException Key length is not supported by this cipher
   */
  @Validate
  protected AbstractCipherEcb(@NotNull final byte[] key)
      throws InvalidKeyException
  {
    if (!Linq.contains(ArrayUtils.box(getKeySizes()), key.length))
      throw new InvalidKeyException("An unsupported key length was provided to the cipher: " + key.length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract int getBlockSize();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract int[] getKeySizes();

  /**
   * {@inheritDoc}
   */
  public abstract void encrypt(byte[] dataIn, byte[] dataOut, int offset, int count);

  /**
   * {@inheritDoc}
   */
  public abstract void decrypt(byte[] dataIn, byte[] dataOut, int offset, int count);

  /**
   * Checks the given arguments for validity
   * 
   * @throws NullPointerException An argument is null
   * @throws IndexOutOfBoundsException An index is out of range
   * @throws IllegalArgumentException An argument is out of range, or the output array is not large enough
   */
  protected void checkArguments(byte[] dataIn, byte[] dataOut, int offset, int count)
  {
    if (dataIn == null)
      throw new NullPointerException("dataIn");
    if (dataOut == null)
      throw new NullPointerException("dataOut");
    if (offset > dataIn.length)
      throw new IndexOutOfBoundsException("offset=" + offset + " dataInLen=" + dataIn.length);
    if (offset > dataOut.length)
      throw new IndexOutOfBoundsException("offset=" + offset + " dataOutLen=" + dataOut.length);
    if(offset + count < 0)
      throw new IllegalArgumentException("offset="+offset+" count="+count);
    if (offset + count > dataIn.length)
      throw new IndexOutOfBoundsException("offset+count=" + offset + count + " dataInLen=" + dataIn.length);
    if (offset + count > dataOut.length)
      throw new IndexOutOfBoundsException("offset+count=" + offset + count + " dataOutLen=" + dataOut.length);

    if (count % getBlockSize() != 0)
      throw new IllegalArgumentException("The data length is not a multiple of the cipher block size (" + getBlockSize() + "): " + count);
  }
}
