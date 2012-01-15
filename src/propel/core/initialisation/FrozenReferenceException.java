package propel.core.initialisation;

/**
 * Signifies that an frozen object reference was attempted to be overwritten.
 */
public class FrozenReferenceException
    extends RuntimeException
{
  private static final String ERROR_ALREADY_SET = "The frozen object has already been set";
  public static final String ERROR_OBJ_ALREADY_SET = "The frozen object%s has already been set";
  private static final long serialVersionUID = -3253497714824011965L;

  /**
   * Default constructor
   */
  public FrozenReferenceException()
  {
    super(ERROR_ALREADY_SET);
  }

  public FrozenReferenceException(String msg)
  {
    super(msg);
  }

  public FrozenReferenceException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}
