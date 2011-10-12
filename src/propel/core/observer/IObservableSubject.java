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
 * The interface of an observable. Notifies all attached observers of events of interest.
 */
public interface IObservableSubject<T>
{
  /**
   * How to behave upon an observer notification exception
   */
  ObserverFailureHandlingMode getObserverFailureHandling();

  /**
   * Returns the total number of observers
   */
  int getObserverCount();

  /**
   * Adds an observer that will get notified when items are changed in a collection.
   */
  void attachObserver(ISubjectObserver<T> observer);

  /**
   * Removes all observers.
   */
  void clearObservers();

  /**
   * Removes a previously added observer, it will no longer be notified of changes in a collection. Returns true if successful, false
   * otherwise.
   */
  boolean detachObserver(ISubjectObserver<T> observer);
}
