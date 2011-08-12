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
 * Class encapsulates a number of cancellable functions which can execute in parallel.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class CancellableTaskCollection<T, TResult>
		extends ParallelTaskCollection<T, TResult>
{
	/**
	* The object that allows for control of task cancellation
	*/
	private ICancellation canceller;

	/**
	 * Default constructor, uses the default cancellation polling interval.
	 */
	public CancellableTaskCollection()
	{
		canceller = new Cancellation();
	}

	/**
	 * Overloaded constructor
	 *
	 * @throws NullPointerException	 An argument is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public CancellableTaskCollection(ICancellation cancellation)
	{
		if(cancellation == null)
			throw new NullPointerException("cancellation");
		if(cancellation.isCancelled())
			throw new IllegalArgumentException("The cancellation object is already cancelled!");

		canceller = cancellation;
	}

	public ICancellation getCanceller()
	{
		return canceller;
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel. Uses a default timeout setting.
	 *
	 * @throws NullPointerException The action is null
	 */
	@Override
	public void addAction(ActionWithOneArgument<T> action, T state)
	{
		addAction(action, state, canceller);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 Action is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void addAction(ActionWithOneArgument<T> action, T state, ICancellation cancellation)
	{
		CancellableTask<T, TResult> task = new CancellableTask<T, TResult>(action, state, results, cancellation);
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
		addFunction(function, state, canceller);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 Function is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void addFunction(FunctionWithOneArgument<T, TResult> function, T state, ICancellation cancellation)
	{
		CancellableTask<T, TResult> task = new CancellableTask<T, TResult>(function, state, results, cancellation);
		tasks.add(task);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 */
	@Override
	public void addParameterlessAction(ActionWithNoArguments action)
	{
		addParameterlessAction(action, canceller);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 Function is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void addParameterlessAction(final ActionWithNoArguments action, ICancellation cancellation)
	{
		addAction(new ActionWithOneArgument<T>(Object.class)
		{
			@Override
			public void executeWith(T arg)
			{
				action.execute();
			}
		}, null, cancellation);
	}

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException An argument is null
	 */
	@Override
	public void addParameterlessFunction(FunctionWithNoArguments<TResult> function)
	{
		addParameterlessFunction(function, canceller);
	}

	/**
	 * Adds a new parameterless function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 Function is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void addParameterlessFunction(final FunctionWithNoArguments<TResult> function, ICancellation cancellation)
	{
		addFunction(new FunctionWithOneArgument<T, TResult>()
		{
			@Override
			public TResult operateOn(T arg)
			{
				return function.operate();
			}
		}, null, cancellation);
	}
}

