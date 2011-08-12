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
import propel.core.collections.queues.SharedQueue;
import propel.core.configuration.ConfigurableConsts;
import propel.core.configuration.ConfigurableParameters;
import propel.core.counters.SharedModuloCounterLight;
import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;

/**
 * A lightweight replacement to the .NET framework's ThreadPool.
 * Capable of executing tasks asynchronously but has added features.
 * Task are executed by background threads, i.e. they exit when all non-background threads exit.
 */
class TaskThreadPool
{
	/**
	 * Thread naming convention: TaskGiverThreadName-T2,
	 * e.g. if MAIN queues the first task, it is run by MAIN-T1
	 */
	private static final String THREAD_ID_PREFIX = ConfigurableParameters.getString(ConfigurableConsts.TASKTHREADPOOL_THREAD_ID_PREFIX);
	/**
	 * Always have this many (initial) threads available to execute tasks for bursts of parallel activity.
	 */
	private static final int MIN_IDLE_THREADS = ConfigurableParameters.getInt32(ConfigurableConsts.TASKTHREADPOOL_MIN_IDLE_THREADS);
	/**
	 * When the available number of threads falls below this threshold, the threadpool will start creating a number of extra threads
	 */
	private static final short MIN_THREAD_REPLENISH_TRIGGER = ConfigurableParameters.getInt16(ConfigurableConsts.TASKTHREADPOOL_MIN_THREAD_REPLENISH_TRIGGER);
	/**
	 * This is the number of threads that the threadpool will create if reached a low thread count threshold that triggers it
	 */
	private static final short THREAD_REPLENISH_COUNT = ConfigurableParameters.getInt16(ConfigurableConsts.TASKTHREADPOOL_THREAD_REPLENISH_COUNT);
	/**
	 * Single instance of the threadpool
	 */
	private static final TaskThreadPool INSTANCE = new TaskThreadPool(MIN_IDLE_THREADS);

	/**
	 * Returns the number of threads that are idle
	 */
	public int getIdle()
	{
		return INSTANCE.idleThreads.size();
	}

	// counter allows for unique thread naming
	private final SharedModuloCounterLight counter;
	// the pool of available threads
	private final ISharedQueue<TaskThread> idleThreads;
	// the thread factory creates task-oriented threads
	private final TaskThreadFactory threadFactory;

	/**
	 * Private constructor
	 */
	private TaskThreadPool(int startingIdleThreads)
	{
		counter = new SharedModuloCounterLight(Short.MAX_VALUE);
		idleThreads = new SharedQueue<TaskThread>(TaskThread.class);
		threadFactory = new TaskThreadFactory(idleThreads);

		// add minimum threads
		threadFactory.create(startingIdleThreads);
	}

	/**
	 * Queues a task for execution.
	 * No state is passed to the task.
	 * The call is non-blocking, i.e. this method returns immediately.
	 */
	public void queueTask(final ActionWithNoArguments action)
	{
		queueTask(new ActionWithOneArgument<Object>()
		{
			@Override
			public void executeWith(Object arg)
			{
				action.execute();
			}
		}, null);
	}

	/**
	 * Queues a task for execution.
	 * A state object may be passed as argument to the task's execution method.
	 * The call is non-blocking, i.e. this method returns immediately.
	 */
	public void queueTask(ActionWithOneArgument<? extends Object> action, Object state)
	{
		TaskThread thread = INSTANCE.getNextIdle();

		// put the waiting thread into runnable state, pass the action to the thread
		thread.queueWork(new TaskThreadWorkUnit(action, state));
	}

	/**
	 * Retrieves a thread to perform a task. If none are available, orders the creation of one.
	 * Also performs housekeeping tasks e.g. renames it for better logging.
	 * Returns the initialized thread.
	 */
	private TaskThread getNextIdle()
	{
		// see if thread creation required
		if(idleThreads.size() < MIN_THREAD_REPLENISH_TRIGGER)
			threadFactory.create(THREAD_REPLENISH_COUNT);

		TaskThread thread = idleThreads.get();
		// name it appropriately, for logging purposes.
		thread.rename(Thread.currentThread().getName() + THREAD_ID_PREFIX + counter.next());

		return thread;
	}

	/**
	 * Singleton instance getter
	 */
	public static TaskThreadPool getInstance()
	{
		return INSTANCE;
	}

}
