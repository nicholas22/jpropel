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
 * POJO encapsulates a single work unit that can be dynamically invoked regardless of whether it is an Action or a Function.
 */
class TaskThreadWorkUnit<T>
{
	/**
	 * The action to be executed
	 */
	public ActionWithOneArgument<T> action;
	/**
	 * The state to be passed to the action
	 */
	public T state;

	/**
	 * Conversion constructor
	 */
	public TaskThreadWorkUnit(ActionWithOneArgument<T> action, T state)
	{
		this.action = action;
		this.state = state;
	}

	public ActionWithOneArgument<T> getAction()
	{
		return action;
	}

	public T getState() {
		return state;
	}
}
