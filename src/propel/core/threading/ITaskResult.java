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

/**
 * Interface of the result created by the work carried out by a task
 */
public interface ITaskResult<TResult>
{
	/**
	 * The source task ID of this result,
	 * can be used to track the task that produced this result.
	 */
	long getTaskId();

	/**
	 * The result of a function.
	 * Actions do not change this field (i.e. it holds the default(TResult) value).
	 * If an exception has occurred then accessing this property throws the exception.
	 *
	 * @throws TaskResultException May throw an exception, if the task that created this result caused an exception instead of creating an actual result.
	 */
	TResult getResult();

	/**
	 * If an exception occurs during a task, it is stored here.
	 * If no exceptions occurred, this is null.
	 */
	Throwable getError();

	/**
	 * Returns true if no exceptions occurred, i.e. the task was successfully completed.
	 */
	boolean isSuccessful();
}
