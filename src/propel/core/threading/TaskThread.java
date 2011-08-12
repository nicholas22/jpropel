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
import propel.core.utils.StringUtils;

/**
 * Re-usable thread that processes tasks as they are being queued in the threadpool.
 * Task threads are background threads, i.e. they exit when all non-background threads exit.
 */
class TaskThread
		implements Runnable
{
	// the thread being encapsulated
	// used for putting a thread that completes its task back
	private final ISharedQueue<TaskThread> idleThreads;
	private final Thread thread;
	// used to put a waiting thread into runnable state
	private final ISharedQueue<TaskThreadWorkUnit> workUnits;

	/**
	 * Initializes with the pool of idle threads,
	 * as unused threads are put back into the pool.
	 */
	public TaskThread(ISharedQueue<TaskThread> idleThreads)
	{
		this.idleThreads = idleThreads;
		workUnits = new SharedQueue<TaskThreadWorkUnit>(TaskThreadWorkUnit.class);
		thread = new Thread(this);
		thread.setDaemon(ConfigurableParameters.getBool(ConfigurableConsts.TASKTHREADPOOL_DAEMON_THREADS));
		thread.start();
	}

	/**
	 * Renames a thread
	 */
	public void rename(String name)
	{
		// rename only if needed
		if(name != null)
		{
			String oldName = thread.getName();
			if(StringUtils.isNullOrEmpty(oldName) || !oldName.equals(name))
				thread.setName(name);
		}
	}

	/**
	 * Processes the given task
	 */
	public void queueWork(TaskThreadWorkUnit workUnit)
	{
		workUnits.put(workUnit);
	}

	/**
	 * This the thread 'Run' method, simply waiting for tasks and processing them.
	 * While there are no tasks, the thread is in the waiting state.
	 */
	public void run()
	{
		while(true)
		{
			TaskThreadWorkUnit workUnit = workUnits.get();
			try
			{
				// invocation
				workUnit.getAction().executeWith(workUnit.getState());
			}
			finally
			{
				// make thread available again
				idleThreads.put(this);
			}
		}
	}
}
