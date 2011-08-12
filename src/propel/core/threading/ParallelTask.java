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
import propel.core.collections.queues.SharedObservableQueue;
import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;
import propel.core.functional.FunctionWithOneArgument;
import propel.core.observer.ISubjectObserver;
import propel.core.observer.ObserverFailureHandlingMode;
import propel.core.utils.RandomUtils;

/**
 * Class encapsulates a single parallel/background function.
 */
public class ParallelTask<T, TResult>
		implements IParallelTask<TResult>
{
	/**
	 * An identifier for this task.
	 */
	private final long id;
	/**
	 * Where results are put
	 */
	private final SharedObservableQueue<ITaskResult<TResult>> results;
	/**
	 * Encapsulates the wrapped action/function's state
	 */
	private final T state;
	/**
	 * Reference to task threadpool
	 */
	final TaskThreadPool threadpool;
	/**
	 * Encapsulates the provided action/function by wrapping around it
	 * and ensuring any results are maintained for possible retrieval.
	 */
	ActionWithOneArgument<T> wrappedAction;

	/**
	 * For private use only.
	 * Initializes with the task's state, if any and the queue where results are put.
	 */
	protected ParallelTask(T state, SharedObservableQueue<ITaskResult<TResult>> results)
	{
		id = RandomUtils.getPseudoInt64();
		this.state = state;
		this.results = results;

		threadpool = TaskThreadPool.getInstance();
	}

	/**
	 * Initializes with the task's action, state and a queue where a default(TResult) object is put when the action completes.
	 *
	 * @throws NullPointerException Action or results is null
	 */
	ParallelTask(final ActionWithOneArgument<T> action, T state, final SharedObservableQueue<ITaskResult<TResult>> results)
	{
		this(state, results);

		if(action == null)
			throw new NullPointerException("action");
		if(results == null)
			throw new NullPointerException("results");

		final long id = getId();
		// wrap within another action which catches exceptions and will execute the original action, then signal completion
		wrappedAction = new ActionWithOneArgument<T>(action.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				TaskResult<TResult> taskResult = new TaskResult<TResult>(id);

				try
				{
					// execute action
					action.executeWith(actionState);
				}
				catch(Throwable e)
				{
					// store error
					taskResult.setError(e);
				}
				finally
				{
					// signal completion
					results.put(taskResult);
				}
			}

			;
		};
	}

	/**
	 * Initializes with the task's function, state and a queue where the function's result is put upon completion.
	 *
	 * @throws NullPointerException Function or results is null
	 */
	ParallelTask(final FunctionWithOneArgument<T, TResult> function, T state, final SharedObservableQueue<ITaskResult<TResult>> results)
	{
		this(state, results);

		if(function == null)
			throw new NullPointerException("function");
		if(results == null)
			throw new NullPointerException("results");

		final long id = getId();
		// wrap within another action which will execute the original action and store results upon completion
		wrappedAction = new ActionWithOneArgument<T>(function.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				{
					TaskResult<TResult> taskResult = new TaskResult<TResult>(id);

					try
					{
						// execute function, store result
						TResult tr = function.operateOn(actionState);
						taskResult.setResult(tr);
					}
					catch(Throwable e)
					{
						// store error
						taskResult.setError(e);
					}
					finally
					{
						// signal completion
						results.put(taskResult);
					}
				}
				;
			}
		};
	}

	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException Action is null
	 */
	protected ParallelTask(final ActionWithNoArguments action)
	{
		this(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class));
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException Action is null
	 */
	protected ParallelTask(ActionWithOneArgument<T> action, T state)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class));
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException Action is null
	 */
	protected ParallelTask(FunctionWithOneArgument<T, TResult> action, T state)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class));
	}

	/**
	 * The task state
	 */
	public T getState()
	{
		return state;
	}

	/**
	 * An identifier for this task
	 */
	@Override
	public long getId()
	{
		return id;
	}

	/**
	 * Executes this task in another thread.
	 * Blocking method, returns a result when execution completes.
	 */
	@Override
	public ITaskResult<TResult> executeAndWait()
	{
		threadpool.queueTask(wrappedAction, state);
		return results.get();
	}

	/**
	 * Executes this task in the background using another thread.
	 * Non-blocking method, returns immediately.
	 * The result is put in the returning queue once ready.
	 */
	@Override
	public ISharedQueue<ITaskResult<TResult>> executeLater()
	{
		threadpool.queueTask(wrappedAction, state);
		return results;
	}

	/**
	 * Executes this task in the background using another thread.
	 * Non-blocking method, returns immediately.
	 * The result is notified to all specified observers, one at a time (non-concurrently).
	 * Uses the specified observer notification failure to handle exceptions during observer notifications.
	 *
	 * @throws NullPointerException Observers argument is null.
	 */
	@Override
	public void executeLater(Iterable<ISubjectObserver<ITaskResult<TResult>>> observers, ObserverFailureHandlingMode observerFailureHandlingMode)
	{
		if(observers == null)
			throw new NullPointerException("observers");

		results.setObserverFailureHandling(observerFailureHandlingMode);

		for(ISubjectObserver<ITaskResult<TResult>> observer : observers)
			results.attachObserver(observer);

		threadpool.queueTask(wrappedAction, state);
	}
}
