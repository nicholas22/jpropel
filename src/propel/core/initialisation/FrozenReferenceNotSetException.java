package propel.core.initialisation;

/**
 * Signifies that a frozen object reference was attempted to be read before being set.
 */
public class FrozenReferenceNotSetException
    extends RuntimeException
{
  public static final String ERROR_NOT_SET = "The frozen object has not been set";
  private static final long serialVersionUID = 5551290810768825459L;

  /**
   * Default constructor
   */
  public FrozenReferenceNotSetException()
  {
    super(ERROR_NOT_SET);
  }

  public FrozenReferenceNotSetException(String msg)
  {
    super(msg);
  }

  public FrozenReferenceNotSetException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}
