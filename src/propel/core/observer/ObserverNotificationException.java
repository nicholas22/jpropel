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
package propel.core.observer;

/**
 * Signifies that a failure to notify an observer has occurred.
 */
public class ObserverNotificationException
    extends RuntimeException
{
  private static final long serialVersionUID = 2327124438166471887L;

  public ObserverNotificationException()
  {
    super();
  }

  public ObserverNotificationException(String msg)
  {
    super(msg);
  }

  public ObserverNotificationException(String msg, Throwable inner)
  {
    super(msg, inner);
  }
}
