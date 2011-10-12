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
package propel.core.security.cryptography;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import propel.core.security.cryptography.ciphers.ICipher;
import propel.core.security.cryptography.ciphers.XTEACipherEcb;
import propel.core.utils.ArrayUtils;
import propel.core.utils.ConversionUtils;
import propel.core.utils.RandomUtils;

/**
 * A string which encrypts the payload it stores.
 */
public final class CryptographicString
{
  // XTEA cipher
  private ICipher cipher;

  /**
   * The payload is held in encrypted form in memory
   */
  private final byte[] encryptedPayload;
  /**
   * The original size of the payload, the encrypted size is divisible by exactly the cipher's block size
   */
  private final int originalPayloadSize;

  /**
   * Initialize with a string payload. Multicultural strings are allowed (UTF8). Only to be used for testing purposes.
   * 
   * @deprecated Do not store passwords in strings! They get interned by the JVM and remain in memory for the duration of program execution.
   */
  @Deprecated
  public CryptographicString(String payload)
  {
    this(ConversionUtils.toByteArray(payload));
  }

  /**
   * Initialize with some byte[] payload. The array is zero-ed once this is done.
   * 
   * @throws NullPointerException An argument is null
   * @throws RuntimeException An exception has occurred by the security provider (BouncyCastle)
   */
  public CryptographicString(byte[] payload)
  {
    if (payload == null)
      throw new NullPointerException("payload");

    // create cipher
    byte[] key = RandomUtils.getSecureBytes(XTEACipherEcb.RECOMMENDED_KEY_SIZE);
    try
    {
      cipher = new XTEACipherEcb(key);
    }
    catch(Exception e)
    {
      Security.addProvider(new BouncyCastleProvider());
      try
      {
        cipher = new XTEACipherEcb(key);
      }
      catch(Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }

    // set payload sizes to match cipher block size
    originalPayloadSize = payload.length;
    int encryptedPayloadSize = originalPayloadSize;
    while (encryptedPayloadSize % cipher.getBlockSize() != 0)
      encryptedPayloadSize++;

    // perform encryption of payload
    payload = ArrayUtils.unbox(ArrayUtils.resize(ArrayUtils.box(payload), encryptedPayloadSize));

    encryptedPayload = new byte[encryptedPayloadSize];
    cipher.encrypt(payload, 0, encryptedPayload, 0, encryptedPayloadSize);
    for (int i = 0; i < payload.length; i++)
      payload[i] = 0;
  }

  /**
   * Return the unencrypted payload as byte[]
   */
  public byte[] asByteArray()
  {
    // decrypt
    byte[] originalPayload = new byte[encryptedPayload.length];
    cipher.decrypt(encryptedPayload, 0, originalPayload, 0, encryptedPayload.length);

    // trim to original size
    originalPayload = ArrayUtils.unbox(ArrayUtils.resize(ArrayUtils.box(originalPayload), originalPayloadSize));
    return originalPayload;
  }

  /**
   * Return the unencrypted payload as char[]
   */
  public char[] asCharArray()
  {
    return ConversionUtils.toString(asByteArray()).toCharArray();
  }

  /**
   * Return the unencrypted payload as a UTF8 string
   * 
   * @deprecated Do not store passwords in strings! They get interned by the JVM and remain in memory for the duration of program execution.
   */
  @Deprecated
  public String asString()
  {
    return ConversionUtils.toString(asByteArray());
  }
}
