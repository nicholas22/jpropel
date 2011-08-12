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

import propel.core.functional.FunctionWithOneArgument;

/**
 * Class encapsulates a number of cancellable functions which can execute in parallel.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class CancellableFunctionCollection<T, TResult>
		extends CancellableTaskCollection<T, TResult>
{
	/**
	 * Default constructor, uses default cancellation polling interval.
	 */
	public CancellableFunctionCollection()
	{
		super(new Cancellation());
	}

	/**
	 * Overloaded constructor
	 *
	 * @throws NullPointerException	 Cancellation is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public CancellableFunctionCollection(ICancellation cancellation)
	{
		super(cancellation);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 An argument is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void add(FunctionWithOneArgument<T, TResult> function, T state, ICancellation cancellation)
	{
		addFunction(function, state, cancellation);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 An argument is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void add(FunctionWithOneArgument<T, TResult> function, T state)
	{
		addFunction(function, state, getCanceller());
	}
}
