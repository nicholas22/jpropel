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

import propel.core.collections.queues.SharedObservableQueue;
import propel.core.functional.FunctionWithOneArgument;

/**
 * Class encapsulates a single parallel/background function.
 */
public class ParallelFunction<T, TResult>
		extends ParallelTask<T, TResult>
{
	/**
	 * Creates a task from a function. A parameter to the function can also be specified.
	 *
	 * @throws NullPointerException Function is null
	 */
	public ParallelFunction(FunctionWithOneArgument<T, TResult> function, T state)
	{
		super(function, state, new SharedObservableQueue<ITaskResult<TResult>>(ITaskResult.class));
	}
}
