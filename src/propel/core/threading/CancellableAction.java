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

/**
 * Class encapsulates a single parallel/background function, which can be stopped before completion.
 * Attention: Cancellation involves thread abortion, therefore be careful when using shared locks with this structure.
 */
public class CancellableAction<T>
		extends CancellableTask<T, Object>
{
	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException Action is null, or cancellation object is null.
	 */
	public CancellableAction(ActionWithNoArguments action)
	{
		super(action, new Cancellation());
	}

	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public CancellableAction(ActionWithNoArguments action, ICancellation cancellation)
	{
		super(action, cancellation);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException Action is null, or cancellation object is null.
	 */
	public CancellableAction(ActionWithOneArgument<T> action, T state)
	{
		super(action, state, new Cancellation());
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException	 Action is null, or cancellation object is null.
	 * @throws IllegalArgumentException The cancellation object is already cancelled
	 */
	public CancellableAction(ActionWithOneArgument<T> action, T state, ICancellation cancellation)
	{
		super(action, state, cancellation);
	}
}
