package propel.core.security.hashing;


/**
 * Represents the interface which all hash implementations implement
 */
public abstract class HashAlgorithm
{
/**
 * Computes the hash value for the specified byte array.
 */
 /* public byte[] computeHash(byte[] data){
    
  }*/
  
  /**
   * Computes the hash value for the specified Stream object.
   */
 /* public byte[] computeHash(InputStream is){
    
  }*/
  
  /**
   * Computes the hash value for the specified byte array.
   */
  /*public byte[] computeHash(byte[] data, int offset, int length){
    
  }*/
  
  /**
   * When overridden in a derived class, routes data written to the object into the hash algorithm for computing the hash.
   */
  //protected abstract void hashCore(byte[] data, int start, int size);

  /**
   * When overridden in a derived class, finalizes the hash computation after the last data is processed by the cryptographic stream object.
   */
  //protected abstract byte[] hashFinal();
}
