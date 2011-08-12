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

import propel.core.functional.ActionWithNoArguments;
import propel.core.functional.ActionWithOneArgument;
import propel.core.functional.FunctionWithNoArguments;
import propel.core.functional.FunctionWithOneArgument;

/**
 * Class encapsulates a number of time-constrained functions which can execute in parallel.
 */
public class TimedTaskCollection<T, TResult>
		extends ParallelTaskCollection<T, TResult>
{
	/**
	 * Default timeout if none is given
	 */
	public static final int DEFAULT_TIMEOUT_MILLIS = 60 * 1000;
	/**
	 * The period of time allowed for the task to finish before it is cancelled.
	 */
	private long timeoutMillis;

	/**
	 * Default constructor, sets the default timeout to 1 minute.
	 */
	public TimedTaskCollection()
	{
		timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
	}

	/**
	 * Overloaded constructor
	 *
	 * @throws IllegalArgumentException Timeout is out of range
	 */
	public TimedTaskCollection(long timeoutMillis)
	{
		if(timeoutMillis < 0)
			throw new IllegalArgumentException("timeoutMillis=" + timeoutMillis);

		this.timeoutMillis = timeoutMillis;
	}

	public long getTimeoutMillis()
	{
		return timeoutMillis;
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException Action is null
	 */
	@Override
	public void addAction(ActionWithOneArgument<T> action, T state)
	{
		addAction(action, state, getTimeoutMillis());
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException Action is null
	 */
	public void addAction(ActionWithOneArgument<T> action, T state, long timeoutMillis)
	{
		TimedTask<T, TResult> task = new TimedTask<T, TResult>(action, state, results, timeoutMillis);
		tasks.add(task);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException Function is null
	 */
	@Override
	public void addFunction(FunctionWithOneArgument<T, TResult> function, T state)
	{
		addFunction(function, state, timeoutMillis);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException Function is null
	 */
	public void addFunction(FunctionWithOneArgument<T, TResult> function, T state, long timeoutMillis)
	{
		TimedTask<T, TResult> task = new TimedTask<T, TResult>(function, state, results, timeoutMillis);
		tasks.add(task);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addParameterlessAction(ActionWithNoArguments action)
	{
		addParameterlessAction(action, timeoutMillis);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException An argument is null
	 */
	public void addParameterlessAction(final ActionWithNoArguments action, long timeoutMillis)
	{
		addAction(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null, timeoutMillis);
	}

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addParameterlessFunction(FunctionWithNoArguments<TResult> function)
	{
		addParameterlessFunction(function, timeoutMillis);
	}

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException An argument is null
	 */
	public void addParameterlessFunction(final FunctionWithNoArguments<TResult> function, long timeoutMillis)
	{
		addFunction(new FunctionWithOneArgument<T, TResult>()
		{
			@Override
			public TResult operateOn(T arg)
			{
				return function.operate();
			}
		}, null, timeoutMillis);
	}
}

