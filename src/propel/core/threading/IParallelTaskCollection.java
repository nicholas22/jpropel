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
import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;
import propel.core.functional.FunctionWithNoArguments;
import propel.core.functional.FunctionWithOneArgument;
import propel.core.observer.ISubjectObserver;
import propel.core.observer.ObserverFailureHandlingMode;

/**
 * Interface for grouping tasks for concurrent / background execution
 */
public interface IParallelTaskCollection<T, TResult>
{
	/**
	 * Returns the current number of tasks added
	 */
	int size();

	/**
	 * Lists the task IDs, in the order that the tasks were added.
	 * This can be used to match task result sources.
	 */
	long[] getTaskIds();

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	void addParameterlessAction(ActionWithNoArguments action);

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	void addParameterlessFunction(FunctionWithNoArguments<TResult> function);

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	void addAction(ActionWithOneArgument<T> action, T state);

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	void addFunction(FunctionWithOneArgument<T, TResult> function, T state);

	/**
	 * Removes all added action/function tasks to allow for re-use.
	 */
	void clear();

	/**
	 * Removes an action/function from the collection
	 *
	 * @throws IndexOutOfBoundsException An index is out of bounds
	 */
	void remove(int index);

	/**
	 * Executes all tasks added in parallel.
	 * Blocks until all tasks are finished.
	 * Do not use execution methods more than once.
	 */
	Iterable<ITaskResult<TResult>> executeAndWaitAll(TaskResultOrder order);

	/**
	 * Executes all tasks added in parallel.
	 * Blocks until one or more tasks (as specified) finish.
	 * The rest of the tasks are ignored but will run to completion.
	 * Do not use execution methods more than once.
	 */
	Iterable<ITaskResult<TResult>> executeAndWait(int count, TaskResultOrder order);

	/**
	 * Executes all tasks added in parallel.
	 * Does not block until all tasks are finished, i.e. returns immediately.
	 * The results are put in the returning (thread-safe) queue as they become ready and are unordered.
	 * Do not use execution methods more than once.
	 */
	ISharedQueue<ITaskResult<TResult>> executeLater();

	/**
	 * Executes all tasks added in parallel.
	 * Does not block until all tasks are finished, i.e. returns immediately.
	 * The results are notified to the specified observer one at a time (non-concurrently).
	 * All observers are notified before the next result is processed.
	 * Uses the specified observer notification failure to handle exceptions during observer notifications.
	 * Do not use execution methods more than once.
	 *
	 * @throws NullPointerException Observers is null.
	 */
	void executeLater(Iterable<ISubjectObserver<ITaskResult<TResult>>> observers, ObserverFailureHandlingMode observerFailureHandlingMode, TaskResultOrder order);

	/**
	 * Executes all tasks added in parallel lazily.
	 * Allows iteration over the results, by yield returning task results.
	 * Do not use execution methods more than once.
	 */
	Iterable<ITaskResult<TResult>> executeAndYield(TaskResultOrder order);
}
