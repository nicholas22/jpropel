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
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class encapsulates a single parallel/background function, which can be stopped before completion.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class CancellableTask<T, TResult>
		extends ParallelTask<T, TResult>
{
	/**
	 * The interval in milliseconds between polling the ICancellable on whether a task should be completed.
	 */
	public final static int DEFAULT_POLLING_INTERVAL_MILLIS = 350;
	/**
	 * The object that allows for control of task cancellation
	 */
	private final ICancellation canceller;
	/**
	 * Used to synchronise (accidental) multiple timer accesses
	 */
	private final ReentrantLock timerLock;

	/**
	 * Initializes with the task's action, state and a queue where a default(TResult) object is put when the action completes.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	CancellableTask(final ActionWithOneArgument<T> action, T state, final SharedObservableQueue<ITaskResult<TResult>> results, final ICancellation cancellation)
	{
		super(state, results);

		if(action == null)
			throw new NullPointerException("action");
		if(cancellation == null)
			throw new NullPointerException("cancellation");
		if(cancellation.isCancelled())
			throw new IllegalArgumentException("The cancellation object is already cancelled!");
		this.canceller = cancellation;
		timerLock = new ReentrantLock();
		long pollingInterval = canceller.getPollingIntervalMillis();

		final long id = getId();
		final long timerDelay = pollingInterval;
		// wrap within another action which catches exceptions and will execute the original action with cancellation capability, then signal completion
		wrappedAction = new ActionWithOneArgument<T>(action.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				TaskResult<TResult> taskResult = new TaskResult<TResult>(id);
				// timer which will call stop() on the executing thread if cancellation is requested by the ICancellation owner
				Timer timer = new Timer(true);
				timer.schedule(new MyTimerTask(Thread.currentThread()), timerDelay);

				try
				{
					// execute action which may be aborted
					action.executeWith(actionState);
				}
				catch(ThreadDeath e)
				{
					// note: if you actually need the thread to die, you must re-throw thread death exception

					// describe actual error
					taskResult.setError(new TaskCancelledException(cancellation, e));
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
					// cancel timer, not needed any longer
					timer.cancel();
				}
			}
		};
	}

	/**
	 * Initializes with the task's function, state and a queue where the function's result is put upon completion.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	CancellableTask(final FunctionWithOneArgument<T, TResult> function, T state, final SharedObservableQueue<ITaskResult<TResult>> results, final ICancellation cancellation)
	{
		super(function, state, results);

		if(function == null)
			throw new NullPointerException("function");
		if(cancellation == null)
			throw new NullPointerException("cancellation");
		if(cancellation.isCancelled())
			throw new IllegalArgumentException("The cancellation object is already cancelled!");
		this.canceller = cancellation;
		this.timerLock = new ReentrantLock();
		long pollingInterval = canceller.getPollingIntervalMillis();

		final long id = getId();
		final long timerDelay = pollingInterval;
		// wrap within another action which catches exceptions and will execute the original action with cancellation capability, then signal completion
		wrappedAction = new ActionWithOneArgument<T>(function.getGenericTypeParameter())
		{
			@Override
			public void executeWith(T actionState)
			{
				TaskResult<TResult> taskResult = new TaskResult<TResult>(id);
				// timer which will call stop() on the executing thread if cancellation is requested by the ICancellation owner
				Timer timer = new Timer(true);
				timer.schedule(new MyTimerTask(Thread.currentThread()), timerDelay);

				try
				{
					// execute function, store result- may be aborted
					taskResult.setResult(function.operateOn(actionState));
				}
				catch(ThreadDeath e)
				{
					// note: if you actually need the thread to die, you must re-throw thread death exception

					// describe actual error
					taskResult.setError(new TaskCancelledException(cancellation, e));
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
					// cancel timer, not needed any longer
					timer.cancel();
				}
			}
		};

	}

	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	protected CancellableTask(final ActionWithNoArguments action, ICancellation cancellation)
	{
		this(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), cancellation);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	protected CancellableTask(ActionWithOneArgument<T> action, T state, ICancellation cancellation)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), cancellation);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	protected CancellableTask(FunctionWithOneArgument<T, TResult> action, T state, ICancellation cancellation)
	{
		this(action, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class), cancellation);
	}

	public ICancellation getCanceller()
	{
		return canceller;
	}

	/**
	 * Privately used class, allows for encapsulating a timer cancellation checker method
	 */
	class MyTimerTask
			extends TimerTask
	{
		/**
		 * Whether the timer doing the polling is stopped
		 */
		private volatile boolean timerStopped;
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
			if(!timerStopped)
			{
				// synchronise access to avoid double aborting a thread
				timerLock.lock();
				try
				{
					if(canceller.isCancelled())
					{
						timerStopped = true;
						parent.stop();
					}
				}
				finally
				{
					timerLock.unlock();
				}
			}
		}
	}
}
