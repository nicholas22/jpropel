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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import propel.core.utils.ArrayUtils;
import propel.core.utils.Linq;

/**
 * Provides common functionality across all ciphers
 */
public abstract class AbstractCipherEcb
    implements ICipher
{

  private static final String PROVIDER_NAME = "BC"; // bouncycastle
  // ciphers
  private final Cipher encryptionCipher;
  private final Cipher decryptionCipher;
  // key
  private final SecretKeySpec secretKeySpec;

  /**
   * Initializes with the secret key
   * 
   * @throws NullPointerException An argument is null
   * @throws NoSuchAlgorithmException Algorithm is not supported. Usually due to lack of security provider (register BouncyCastle first)
   * @throws NoSuchProviderException Provided not supported. Usually due to lack of security provider (register BouncyCastle first)
   * @throws NoSuchPaddingException This will not be thrown, unless the padding type has changed
   * @throws InvalidKeyException Key length is not supported by this cipher
   */
  protected AbstractCipherEcb(byte[] key)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException
  {
    if (key == null)
      throw new NullPointerException("key");

    if (!Linq.contains(ArrayUtils.box(getKeySizes()), key.length))
      throw new InvalidKeyException("An unsupported key length was provided to the cipher: " + key.length);

    encryptionCipher = Cipher.getInstance(getCipherDescription(), PROVIDER_NAME);
    decryptionCipher = Cipher.getInstance(getCipherDescription(), PROVIDER_NAME);

    secretKeySpec = new SecretKeySpec(key, getCipherKeySpecName());
  }

  /**
   * Gets the secret key spec
   */
  protected SecretKeySpec getSecretKeySpec()
  {
    return secretKeySpec;
  }

  /**
   * Returns the cipher description e.g. AES/ECB/ZeroBytePadding
   */
  public abstract String getCipherDescription();

  /**
   * Return the cipher secret key spec name e.g. AES
   */
  public abstract String getCipherKeySpecName();

  /**
   * The cipher block size
   */
  @Override
  public abstract int getBlockSize();

  /**
   * The supported key sizes
   */
  @Override
  public abstract int[] getKeySizes();

  /**
   * {@inheritDoc}
   */
  @Override
  public void encrypt(byte[] dataIn, int posIn, byte[] dataOut, int posOut, int count)
  {
    checkArguments(dataIn, posIn, dataOut, posOut, count);

    try
    {
      encryptionCipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());

      // encrypt the provided block
      int totalEncrypted = 0;
      while (totalEncrypted != count)
      {
        // loop until all data encrypted
        int encrypted = encryptionCipher.update(dataIn, posIn + totalEncrypted, count - totalEncrypted, dataOut, posOut + totalEncrypted);
        totalEncrypted += encrypted;
      }
    }
    catch(ShortBufferException e)
    {
      throw new RuntimeException("checkArguments() did not check the arguments correctly", e);
    }
    catch(InvalidKeyException e)
    {
      throw new RuntimeException("An invalid key was given during cipher init", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void decrypt(byte[] dataIn, int posIn, byte[] dataOut, int posOut, int count)
  {
    checkArguments(dataIn, posIn, dataOut, posOut, count);

    try
    {
      decryptionCipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());

      // decrypt the provided block
      int totalDecrypted = 0;
      while (totalDecrypted != count)
      {
        // loop until all data decrypted
        int decrypted = decryptionCipher.update(dataIn, posIn + totalDecrypted, count - totalDecrypted, dataOut, posOut + totalDecrypted);
        totalDecrypted += decrypted;
      }
    }
    catch(ShortBufferException e)
    {
      throw new RuntimeException("checkArguments() did not check the arguments correctly", e);
    }
    catch(InvalidKeyException e)
    {
      throw new RuntimeException("An invalid key was given during cipher init", e);
    }
  }

  /**
   * Encrypts the provided in data from the in start position, puts it in the out data array starting at the out position. Does this for a
   * specified byte count, which must be a multiple of the cipher's block size.
   * 
   * @throws NullPointerException An argument is null
   * @throws IndexOutOfBoundsException An index is out of range
   * @throws IllegalArgumentException An argument is out of range, or the output array is not large enough
   */
  protected void checkArguments(byte[] dataIn, int posIn, byte[] dataOut, int posOut, int count)
  {
    if (dataIn == null)
      throw new NullPointerException("dataIn");
    if (dataOut == null)
      throw new NullPointerException("dataOut");
    if (posIn < 0 || posIn > dataIn.length)
      throw new IndexOutOfBoundsException("posIn=" + posIn + " dataInLen=" + dataIn.length);
    if (posOut < 0 || posOut > dataOut.length)
      throw new IndexOutOfBoundsException("posOut=" + posOut + " dataOutLen=" + dataOut.length);
    if (posIn + count > dataIn.length)
      throw new IllegalArgumentException("The input array is not large enough (" + posIn + "+" + count + ">" + dataIn.length + ")");
    if (posOut + count > dataOut.length)
      throw new IllegalArgumentException("The output array is not large enough (" + posOut + "+" + count + ">" + dataOut.length + ")");
    if (count % getBlockSize() != 0)
      throw new IllegalArgumentException("The data length is not a multiple of the cipher block size (" + getBlockSize() + "): " + count);
  }
}
