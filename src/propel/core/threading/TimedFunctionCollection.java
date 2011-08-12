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
 * Class encapsulates a number of time-constrained functions which can execute in parallel.
 */
public class TimedFunctionCollection<T, TResult>
		extends TimedTaskCollection<T, TResult>
{
	/**
	 * Default constructor, sets the default timeout to 1 minute.
	 */
	protected TimedFunctionCollection()
	{
	}

	/**
	 * Overloaded constructor
	 *
	 * @throws IllegalArgumentException Timeout is out of range
	 */
	public TimedFunctionCollection(long timeoutMillis)
	{
		super(timeoutMillis);
	}

	/**
	 * Adds a new function to the list of tasks to execute in parallel.
	 */
	public void add(FunctionWithOneArgument<T, TResult> function, T state, long timeoutMillis)
	{
		addFunction(function, state, timeoutMillis);
	}
}
