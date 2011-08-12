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
 * Class encapsulates a single parallel/background action.
 */
public class ParallelAction<T>
		extends ParallelTask<T, Object>
{
	/**
	 * Creates a task from a parameterless action.
	 *
	 * @throws NullPointerException The action is null
	 */
	public ParallelAction(ActionWithNoArguments action)
	{
		super(action);
	}

	/**
	 * Creates a task from an action. A parameter to the action can also be specified.
	 *
	 * @throws NullPointerException The action is null
	 */
	public ParallelAction(ActionWithOneArgument<T> action, T state)
	{
		super(action, state);
	}
}
