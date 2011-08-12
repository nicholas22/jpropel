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

/**
 * Factory that manages TaskThread creation.
 */
class TaskThreadFactory
{
	// where newly created threads are put
	private final ISharedQueue<TaskThread> idleThreads;

	/**
	 * Constructor initializes with the thread creation output channel,
	 * i.e. where the threads that are created will be put.
	 *
	 * @throws NullPointerException An argument is null
	 */
	public TaskThreadFactory(ISharedQueue<TaskThread> idleThreads)
	{
		if(idleThreads == null)
			throw new NullPointerException("idleThreads");

		this.idleThreads = idleThreads;
	}

	/**
	 * Creates a number of new threads and puts them in the threads queue.
	 */
	public void create(int count)
	{
		// TODO: asynchronous creation of threads
		for(int i = 0; i < count; i++)
		{
			// create and make it available
			TaskThread tt = new TaskThread(idleThreads);
			idleThreads.put(tt);
		}
	}
}
