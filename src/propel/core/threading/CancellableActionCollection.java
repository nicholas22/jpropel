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

import propel.core.functional.ActionWithOneArgument;

/**
 * Class encapsulates a number of tasks which can execute in Timed.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class CancellableActionCollection<T>
		extends CancellableTaskCollection<T, Object>
{
	/**
	 * Default constructor, uses default cancellation polling interval.
	 */
	public CancellableActionCollection()
	{
		super(new Cancellation());
	}

	/**
	 * Overloaded constructor
	 *
	 * @throws NullPointerException	 An argument is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public CancellableActionCollection(ICancellation cancellation)
	{
		super(cancellation);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 An argument is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void add(ActionWithOneArgument<T> action, T state, ICancellation cancellation)
	{
		addAction(action, state, cancellation);
	}

	/**
	 * Adds a new action to the list of tasks to execute in parallel.
	 *
	 * @throws NullPointerException	 An argument is null
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public void add(ActionWithOneArgument<T> action, T state)
	{
		addAction(action, state, this.getCanceller());
	}
}
