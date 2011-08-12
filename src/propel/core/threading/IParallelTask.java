/*
 ///////////////////////////////////////////////////////////
 //  This file is part of Propel.
 //
 //  Propel is free software: you can redistribute it and/or modify
 //  it under the terms of the GNU Lesser General Public License as published by
 //  the Free Software Foundation, either version 3 of the License, or
 //  (at your option) any later version.
 //
 //  Propel is distributed in the hope that it will be useful,
 //  but WITHOUT ANY WARRANTY; without even the implied warranty of
 //  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 //  GNU Lesser General Public License for more details.
 //
 //  You should have received a copy of the GNU Lesser General Public License
 //  along with Propel.  If not, see <http://www.gnu.org/licenses/>.
 ///////////////////////////////////////////////////////////
 //  Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
 ///////////////////////////////////////////////////////////
 */
package propel.core.threading;

import propel.core.collections.queues.ISharedQueue;
import propel.core.observer.ISubjectObserver;
import propel.core.observer.ObserverFailureHandlingMode;

import java.util.List;

/**
 * The interface of a parallel task
 */
public interface IParallelTask<TResult>
{
	/**
	 * An identifier for this task
	 */
	long getId();

	/**
	 * Executes this task in another thread.
	 * Blocking method, returns a result when execution completes.
	 */
	ITaskResult<TResult> executeAndWait();

	/**
	 * Executes this task in the background using another thread.
	 * Non-blocking method, returns immediately.
	 * The result is put in the returning queue once ready.
	 */
	ISharedQueue<ITaskResult<TResult>> executeLater();

	/**
	 * Executes this task in the background using another thread.
	 * Non-blocking method, returns immediately.
	 * The result is notified to all specified observers, one at a time (non-concurrently).
	 * Uses the specified observer notification failure to handle exceptions during observer notifications.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	void executeLater(Iterable<ISubjectObserver<ITaskResult<TResult>>> observers, ObserverFailureHandlingMode observerFailureHandlingMode);
}
