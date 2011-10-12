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
package propel.core.collections.queues;

import propel.core.observer.IObservableSubject;
import propel.core.observer.ISubjectObserver;
import propel.core.observer.ObserverFailureHandlingMode;
import propel.core.observer.ObserverNotificationException;
import propel.core.utils.SuperTypeTokenException;
import java.util.ArrayList;
import java.util.List;

/**
 * A shared queue for use by multiple threads.
 */
public class SharedObservableQueue<T>
    extends SharedQueue<T>
    implements IObservableSubject<T>
{
  /**
   * This is sent to observers upon an item addition
   */
  public final String ITEM_ADDED = "ItemAdded";
  /**
   * This is sent to observers upon an item removal
   */
  public final String ITEM_REMOVED = "ItemRemoved";
  // private fields
  private final List<ISubjectObserver<T>> observers;
  /**
   * How to behave upon a observer notification exceptions. Not thread-safe, set with care!
   */
  private ObserverFailureHandlingMode observerFailureHandling;

  /**
   * Default constructor. The default observer failure behaviour is to throw the exception. See ObserverFailureHandling for more details.
   * 
   * @throws SuperTypeTokenException When called without using anonymous class semantics.
   */
  public SharedObservableQueue()
  {
    this(ObserverFailureHandlingMode.ThrowOnError);
  }

  /**
   * Default constructor
   * 
   * @throws SuperTypeTokenException When called without using anonymous class semantics.
   */
  public SharedObservableQueue(ObserverFailureHandlingMode observerNotificationFailureHandling)
  {
    super();
    observers = new ArrayList<ISubjectObserver<T>>(16);
    observerFailureHandling = observerNotificationFailureHandling;

    // check behaviour type is recognized
    switch(observerNotificationFailureHandling)
    {
      case ThrowOnError:
      case IgnoreErrors:
      case RemoveObserver:
        break;
      default:
        throw new IllegalArgumentException("Unrecognized observer failure behaviour handler: " + observerNotificationFailureHandling);
    }
  }

  /**
   * Constructor for initializing with the generic type parameter
   * 
   * @throws NullPointerException When the generic type parameter is null.
   */
  public SharedObservableQueue(Class<?> genericTypeParameter)
  {
    this(ObserverFailureHandlingMode.ThrowOnError, genericTypeParameter);
  }

  /**
   * Default constructor
   * 
   * @throws NullPointerException When the generic type parameter is null.
   */
  public SharedObservableQueue(ObserverFailureHandlingMode observerNotificationFailureHandling, Class<?> genericTypeParameter)
  {
    super(genericTypeParameter);
    observers = new ArrayList<ISubjectObserver<T>>(16);
    observerFailureHandling = observerNotificationFailureHandling;

    // check behaviour type is recognized
    switch(observerNotificationFailureHandling)
    {
      case ThrowOnError:
      case IgnoreErrors:
      case RemoveObserver:
        break;
      default:
        throw new IllegalArgumentException("Unrecognized observer failure behaviour handler: " + observerNotificationFailureHandling);
    }
  }

  public ObserverFailureHandlingMode getObserverFailureHandling()
  {
    return observerFailureHandling;
  }

  public void setObserverFailureHandling(ObserverFailureHandlingMode observerFailureHandling)
  {
    this.observerFailureHandling = observerFailureHandling;
  }

  /**
   * Clears the queue after notifying all observers of this. This is an O(n) operation in terms of elements and O(n) in terms of observers.
   * 
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  @Override
  public void clear()
  {
    lock();
    try
    {
      for (T item : queue.toArray())
      {
        // remove item
        queue.removeFirst();

        // notify all observers of item removal
        if (observers.size() > 0)
          notifyRemoved(item);
      }
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Enqueues an object. This is an O(1) operation in terms of elements and O(n) in terms of observers.
   * 
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  @Override
  public void put(T item)
  {
    lock();
    try
    {
      // add the item
      queue.addLast(item);

      // notify all observers of item addition
      if (observers.size() > 0)
        notifyAdded(item);

      notEmpty.signalAll();
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Enqueues a number of objects. This is an O(n) operation in terms of elements and O(n) in terms of observers.
   * 
   * @throws NullPointerException When the argument is null.
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  @Override
  public void putRange(Iterable<? extends T> items)
  {
    if (items == null)
      throw new NullPointerException("items");

    lock();
    try
    {
      for (T item : items)
      {
        // add the item
        queue.addLast(item);

        // notify all observers of item addition
        if (observers.size() > 0)
          notifyAdded(item);
      }

      notEmpty.signalAll();
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Dequeues an object, otherwise blocks until one becomes available. This is an O(1) operation in terms of elements and O(n) in terms of
   * observers.
   * 
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  @Override
  public T get()
  {
    lock();
    try
    {
      // wait until one becomes available
      while (queue.size() <= 0)
      {
        try
        {
          notEmpty.await();
        }
        catch(InterruptedException e)
        {
          continue;
        }
      }

      // remove
      T item = queue.removeFirst();

      // notify all observers of item removal
      if (observers.size() > 0)
        notifyRemoved(item);

      return item;
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Dequeues a number of objects, blocking if next are not available. This is an O(n) operation in terms of elements and O(n) in terms of
   * observers.
   * 
   * @throws IllegalArgumentException Count is out of range.
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  @Override
  public Iterable<T> getRange(int count)
  {
    if (count < 0)
      throw new IllegalArgumentException("count");

    List<T> result = new ArrayList<T>(count);

    lock();
    try
    {
      for (int i = 0; i < count; i++)
      {
        // wait until one becomes available
        while (queue.size() <= 0)
        {
          try
          {
            notEmpty.await();
          }
          catch(InterruptedException e)
          {
            continue;
          }
        }

        // remove
        T item = queue.removeFirst();

        // notify all observers of item removal
        if (observers.size() > 0)
          notifyRemoved(item);

        // add to result list
        result.add(item);
      }
    }
    finally
    {
      unlock();
    }

    return result;
  }

  /**
   * Returns the total number of observers
   */
  public int getObserverCount()
  {
    return observers.size();
  }

  /**
   * Adds an observer that will get notified when items are changed in a collection. This is an O(1) operation.
   */
  public void attachObserver(ISubjectObserver<T> observer)
  {
    lock();
    try
    {
      observers.add(observer);
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Removes a previously added observer, it will no longer be notified of changes in a collection. Returns true if successful, false
   * otherwise. This is an O(n) operation.
   */
  public boolean detachObserver(ISubjectObserver<T> observer)
  {
    lock();
    try
    {
      return observers.remove(observer);
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Removes all observers. This is an O(1) operation.
   */
  public void clearObservers()
  {
    lock();
    try
    {
      observers.clear();
    }
    finally
    {
      unlock();
    }
  }

  /**
   * Notifies all observers of item removal
   * 
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  private void notifyRemoved(T item)
  {
    // notify all observers of item removal
    for (int i = 0; i < observers.size(); i++)
    {
      try
      {
        observers.get(i).observerNotify(ITEM_REMOVED, item);
      }
      catch(Throwable e)
      {
        switch(observerFailureHandling)
        {
          case ThrowOnError:
            throw new ObserverNotificationException("Failed to notify observer #" + i + " of an item removal", e);
          case IgnoreErrors:
            break;
          case RemoveObserver:
            observers.remove(i);
            i--;
            break;
          default:
            throw new IllegalArgumentException("Unrecognized observer failure behaviour handler: " + observerFailureHandling);
        }
      }
    }
  }

  /**
   * Notifies all observers of item addition
   * 
   * @throws ObserverNotificationException When an observer fails to be notified and the notification failure behaviour is set to throw the
   *           exception.
   */
  private void notifyAdded(T item)
  {
    for (int i = 0; i < observers.size(); i++)
    {
      try
      {
        observers.get(i).observerNotify(ITEM_ADDED, item);
      }
      catch(Throwable e)
      {
        switch(observerFailureHandling)
        {
          case ThrowOnError:
            throw new ObserverNotificationException("Failed to notify observer #" + i + " of an item addition", e);
          case IgnoreErrors:
            break;
          case RemoveObserver:
            observers.remove(i);
            i--;
            break;
          default:
            throw new IllegalArgumentException("Unrecognized observer failure behaviour handler: " + observerFailureHandling);
        }
      }
    }
  }
}
