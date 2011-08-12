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

import propel.core.collections.maps.avl.AvlHashtable;
import propel.core.collections.queues.ISharedQueue;
import propel.core.collections.queues.SharedObservableQueue;
import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;
import propel.core.functional.FunctionWithNoArguments;
import propel.core.functional.FunctionWithOneArgument;
import propel.core.observer.ISubjectObserver;
import propel.core.observer.ObserverFailureHandlingMode;
import propel.core.utils.ArrayUtils;
import propel.core.utils.Linq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class encapsulates a number of tasks which can execute in parallel.
 */
public class ParallelTaskCollection<T, TResult>
		implements IParallelTaskCollection<T, TResult>
{
	/**
	 * Shared queue where results or signals of completion from all tasks are put
	 */
	protected final SharedObservableQueue<ITaskResult<TResult>> results;
	/**
	 * The tasks to execute, in the order they are added
	 */
	protected final List<ParallelTask<T, TResult>> tasks;
	/**
	 * A task threadpool reference
	 */
	final TaskThreadPool threadpool;

	/**
	 * Default constructor.
	 */
	public ParallelTaskCollection()
	{
		threadpool = TaskThreadPool.getInstance();
		tasks = new ArrayList<ParallelTask<T, TResult>>(64);
		results = new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class);
	}

	/**
	 * Returns the current number of tasks added
	 */
	@Override
	public int size()
	{
		return tasks.size();
	}

	/**
	 * Lists the task IDs, in the order that the tasks were added.
	 * This can be used to match task result sources.
	 */
	@Override
	public long[] getTaskIds()
	{
		Iterable<Long> result = Linq.select(tasks, new FunctionWithOneArgument<ParallelTask<T, TResult>, Long>()
		{
			@Override
			public Long operateOn(ParallelTask<T, TResult> arg)
			{
				return arg.getId();
			}
		});

		return ArrayUtils.unboxLongs(result);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addParameterlessAction(final ActionWithNoArguments action)
	{
		addAction(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null);
	}

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addParameterlessFunction(final FunctionWithNoArguments<TResult> function)
	{
		addFunction(new FunctionWithOneArgument<T, TResult>()
		{
			@Override
			public TResult operateOn(T arg)
			{
				return function.operate();
			}
		}, null);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addAction(ActionWithOneArgument<T> action, T state)
	{
		tasks.add(new ParallelTask<T, TResult>(action, state, results));
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addFunction(FunctionWithOneArgument<T, TResult> function, T state)
	{
		tasks.add(new ParallelTask<T, TResult>(function, state, results));
	}

	/**
	 * Removes all added action/function tasks to allow for re-use.
	 */
	@Override
	public void clear()
	{
		tasks.clear();
	}

	/**
	 * Removes an action/function from the collection
	 *
	 * @throws IndexOutOfBoundsException An index is out of bounds
	 */
	@Override
	public void remove(int index)
	{
		if(index < 0 || index >= tasks.size())
			throw new IndexOutOfBoundsException("index=" + index + " size=" + tasks.size());

		tasks.remove(index);
	}

	/**
	 * Executes all tasks added in parallel.
	 * Blocks until all tasks are finished.
	 * Do not use execution methods more than once.
	 */
	@Override
	public Iterable<ITaskResult<TResult>> executeAndWaitAll(TaskResultOrder order)
	{
		List<ITaskResult<TResult>> list = new ArrayList<ITaskResult<TResult>>(size());

		// iterate through all results to block until everything is complete
		for(ITaskResult<TResult> result : executeAndYield(order))
			list.add(result);

		return list;
	}

	/**
	 * Executes all tasks added in parallel.
	 * Blocks until one or more tasks (as specified) finish.
	 * The rest of the tasks are ignored but will run to completion.
	 * Do not use execution methods more than once.
	 *
	 * @throws IllegalArgumentException Count is out of range
	 */
	public Iterable<ITaskResult<TResult>> executeAndWait(int count, TaskResultOrder order)
	{
		if(count < 0 || count > tasks.size())
			throw new IllegalArgumentException("count=" + count + " size=" + tasks.size());

		List<ITaskResult<TResult>> list = new ArrayList<ITaskResult<TResult>>(count);
		Iterator<ITaskResult<TResult>> iterator = executeAndYield(order).iterator();

		for(int i = 0; i < count; i++)
			list.add(iterator.next());

		return list;
	}

	/**
	 * Executes all tasks added in parallel.
	 * Does not block until all tasks are finished, i.e. returns immediately.
	 * The results are put in the returning (thread-safe) queue as they become ready and are unordered.
	 * Do not use execution methods more than once.
	 */
	public ISharedQueue<ITaskResult<TResult>> executeLater()
	{
		// starts initial tasks
		for(int i = 0; i < tasks.size(); i++)
		{
			ParallelTask<T, TResult> task = tasks.get(i);
			threadpool.queueTask(task.wrappedAction, task.getState());
		}

		return results;
	}

	/**
	 * Executes all tasks added in parallel.
	 * Does not block until all tasks are finished, i.e. returns immediately.
	 * The results are notified to the specified observer one at a time (non-concurrently).
	 * All observers are notified before the next result is processed.
	 * Uses the specified observer notification failure to handle exceptions during observer notifications.
	 * Do not use execution methods more than once.
	 *
	 * @throws NullPointerException Observers is null
	 */
	public void executeLater(Iterable<ISubjectObserver<ITaskResult<TResult>>> observers, ObserverFailureHandlingMode observerFailureHandlingMode, final TaskResultOrder order)
	{
		if(observers == null)
			throw new NullPointerException("observers");

		results.setObserverFailureHandling(observerFailureHandlingMode);

		for(ISubjectObserver<ITaskResult<TResult>> observer : observers)
			results.attachObserver(observer);

		// execute on another thread
		threadpool.queueTask(new ActionWithOneArgument<IParallelTaskCollection<T, TResult>>()
		{
			@Override
			public void executeWith(IParallelTaskCollection<T, TResult> arg)
			{
				arg.executeAndWaitAll(order);
			}
		}, this);
	}

	/**
	 * Executes all tasks added in parallel lazily.
	 * Allows iteration over the results, by yield returning task results.
	 * NOTE: Unfortunately Java does not support yield iteration so this returns only once all results are in...
	 * <p/>
	 * Do not use execution methods more than once.
	 */
	public Iterable<ITaskResult<TResult>> executeAndYield(TaskResultOrder order)
	{
		List<ITaskResult<TResult>> result = new ArrayList<ITaskResult<TResult>>(tasks.size());
		ISharedQueue<ITaskResult<TResult>> taskResults = this.executeLater();

		switch(order)
		{
			case None:
				// return results as they come in (unordered)
				for(int i = 0; i < tasks.size(); i++)
					// waits until a task is complete
					result.add(taskResults.get());
				break;

			case Ordered:
			case ReverseOrder:

				// lookup is used to temporarily store results that come in out of order, until they become next to be returned
				AvlHashtable<Long, ITaskResult<TResult>> storedTaskResults = new AvlHashtable<Long, ITaskResult<TResult>>(Long.class, ITaskResult.class);

				// this is in the order that tasks were added
				List<Long> taskIDsToReturn = new ArrayList<Long>();
				for(long id : getTaskIds())
					taskIDsToReturn.add(id);

				// reverse if required
				if(order == TaskResultOrder.ReverseOrder)
					Collections.reverse(taskIDsToReturn);

				// return results in ordered fashion
				for(int i = 0; i < tasks.size(); i++)
				{
					// get next available result
					ITaskResult<TResult> taskResult = results.get();
					// the actual next task ID expected for ordered results
					long creatorId = taskIDsToReturn.get(0);

					// check if result has the next expected task ID
					if(taskResult.getTaskId() == creatorId)
					{
						// hit, found the next to return
						taskIDsToReturn.remove(0);
						result.add(taskResult);
					}
					else
					{
						// store for now
						storedTaskResults.add(taskResult.getTaskId(), taskResult);

						// also see if next expected is held due to a previous store operation
						if(storedTaskResults.containsKey(creatorId))
						{
							ITaskResult<TResult> found = storedTaskResults.get(creatorId);
							storedTaskResults.remove(creatorId);
							taskIDsToReturn.remove(creatorId);
							result.add(found);
						}
					}
				}

				// return remaining
				for(int i = 0; i < taskIDsToReturn.size(); i++)
				{
					long taskId = taskIDsToReturn.get(i);
					if(storedTaskResults.containsKey(taskId))
						result.add(storedTaskResults.get(taskId));
				}

				break;
			default:
				throw new IllegalArgumentException("Unrecognized task result order specified: " + order);
		}

		return result;
	}
}
