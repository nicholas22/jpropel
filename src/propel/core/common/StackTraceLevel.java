package propel.core.common;

public enum StackTraceLevel
{
  /**
   * Use this to capture the entire stack trace
   */
  FULL,
  /**
   * Use this to capture only a small part of stack traces
   */
  ABBREVIATED,
  /**
   * Use this to capture only types and messages
   */
  MINIMAL
}
