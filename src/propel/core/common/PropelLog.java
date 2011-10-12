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
package propel.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper over Log4J providing easier logging initialization and method call support
 */
public final class PropelLog
{
  /**
   * Reference to the logger
   */
  private static Logger LOGGER;

  /**
   * Static constructor, initializes the logger
   */
  static
  {
    // init logger
    LOGGER = LoggerFactory.getLogger("propel");

    if (LOGGER == null)
    {
      System.err.println("WARNING: The Propel logger could not be found!");
      System.out.println("WARNING: The Propel logger could not be found!");
    } else
      LOGGER.debug("Logger init");
  }

  /**
   * Logs a DEBUG level message
   */
  public static void debug(String text)
  {
    LOGGER.debug(text);
  }

  /**
   * Logs a WARN level message
   */
  public static void warn(String text)
  {
    LOGGER.warn(text);
  }

  /**
   * Logs an INFO level message
   */
  public static void info(String text)
  {
    LOGGER.info(text);
  }

  /**
   * Logs an ERROR level message
   */
  public static void error(String text)
  {
    LOGGER.error(text);
  }

  /**
   * Logs a FATAL level message
   */
  public static void fatal(String text)
  {
    LOGGER.error("FATAL: " + text);
  }
}
