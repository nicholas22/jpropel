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

import propel.core.collections.queues.SharedObservableQueue;
import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;
import propel.core.functional.FunctionWithOneArgument;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class encapsulates a single parallel/background function,
 * having a specified amount of time to complete, after which it is stopped.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class TimedTask<T, TResult>
		extends ParallelTask<T, TResult>
{
	/**
	 * The amount of time allowed for the task to finish (in milliseconds)
	 */
	private long timeoutMillis;

	/**
	 * Initializes with the task's action, state and a queue where a default(TResult) object is put when the action completes.
	 *
	 * @throws NullPointerException	 Action or results is null
	 * @throws IllegalArgumentException Timeout is out of range.
	 */
	TimedTask(final ActionWithOneArgument<T> action, T state, final SharedObservableQueue<ITaskResult<TResult>> results, final long timeoutMillis)
	{
		super(state, results);

		if(action == null)
			throw new NullPointerException("action");
		if(timeoutMillis < 0)
			throw new IllegalArgumentException("timeoutMillis");
		if(results == null)
			throw new NullPointerException("results");

		this.timeoutMillis = timeoutMillis;

		final long id = getId();
		// wrap within another action which catches exceptions and will execute the original action with time constraints, then signal completion
		wrappedAction = new ActionWithOneArgument<T>(action.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				TaskResult<TResult> taskResult = new TaskResult<TResult>(id);

				// timer which will call Abort() on the executing thread if it times out after the specified Timeout
				Timer timer = new Timer(true);
				timer.schedule(new MyTimerTask(Thread.currentThread()), timeoutMillis);

				try
				{
					// execute action which may be aborted
					action.executeWith(actionState);
				}
				catch(ThreadDeath e)
				{
					// describe actual error
					taskResult.setError(new TaskTimeoutException("Task timed out after " + timeoutMillis + "ms.", e));
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
					// release timer
					timer.cancel();
				}
			}
		};

	}

	/**
	 * Initializes with the task's function, state and a queue where the function's result is put upon completion.
	 *
	 * @throws NullPointerException	 Action or results is null
	 * @throws IllegalArgumentException Timeout is out of range.
	 */
	TimedTask(final FunctionWithOneArgument<T, TResult> function, T state, final SharedObservableQueue<ITaskResult<TResult>> results, final long timeoutMillis)
	{
		super(function, state, results);

		if(function == null)
			throw new NullPointerException("function");
		if(results == null)
			throw new NullPointerException("results");
		if(timeoutMillis < 0)
			throw new IllegalArgumentException("timeoutMillis");
		this.timeoutMillis = timeoutMillis;

		final long id = getId();
		// wrap within another action which catches exceptions and will execute the original action with time constraints, then signal completion
		wrappedAction = new ActionWithOneArgument<T>(function.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				TaskResult<TResult> taskResult = new TaskResult<TResult>(id);

				// timer which will call Abort() on the executing thread if it times out after the specified Timeout
				Timer timer = new Timer(true);
				timer.schedule(new MyTimerTask(Thread.currentThread()), timeoutMillis);

				{
					try
					{
						// execute function, store result- may be aborted
						taskResult.setResult(function.operateOn(actionState));
					}
					catch(ThreadDeath e)
					{
						// describe actual error
						taskResult.setError(new TaskTimeoutException("Task timed out after " + timeoutMillis + "ms.", e));
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
			}

			;
		};
	}

	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException Action is null
	 */
	protected TimedTask(final ActionWithNoArguments action, long timeoutMillis)
	{
		this(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), timeoutMillis);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException Action is null
	 */
	protected TimedTask(ActionWithOneArgument<T> action, T state, long timeoutMillis)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), timeoutMillis);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException	 Action is null
	 * @throws IllegalArgumentException Timeout if out of range
	 */
	protected TimedTask(FunctionWithOneArgument<T, TResult> action, T state, long timeoutMillis)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), timeoutMillis);
	}

	public long getTimeoutMillis()
	{
		return timeoutMillis;
	}

	/**
	 * Privately used class, allows for encapsulating a thread cancellation method
	 */
	class MyTimerTask
			extends TimerTask
	{
		/**
		 * Whether the timer doing the polling is stopped
		 */
		private final Thread parent;

		/**
		 * Initializes with the thread that created this timer task
		 */
		public MyTimerTask(Thread parent)
		{
			this.parent = parent;
		}

		/**
		 * Aborts the provided thread if the task is cancelled.
		 */
		@Override
		@SuppressWarnings("deprecation")
		public void run()
		{
			parent.stop();
		}
	}
}
