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
 * Encapsulates a task's result, which may be an object of TResult or an Exception thrown by the task.
 */
class TaskResult<TResult>
		implements ITaskResult<TResult>
{
	/**
	 * If an exception occurs during a task, it is stored here.
	 * Otherwise this is null.
	 */
	private Throwable error;
	/**
	 * The result of a Function.
	 * Actions do not change this field (i.e. it holds the default (null) value).
	 * If an exception occurs then again this field is not changed.
	 */
	private TResult result;
	/**
	 * The source task ID of this result,
	 * can be used to track the task that produced this result.
	 */
	public long taskId;

	/**
	 * Default constructor
	 */
	public TaskResult()
	{
	}

	/**
	 * Initializes with the task's source identifier
	 */
	TaskResult(long source)
	{
		this();
		taskId = source;
	}

	public long getTaskId()
	{
		return taskId;
	}

	/**
	 * The result of a function.
	 * Actions do not change this field (i.e. it holds the default (null) value), but functions do set this field.
	 * If an exception has occurred then accessing this property throws the exception.
	 *
	 * @throws TaskResultException May throw an exception, if the task that created this result caused an exception instead of creating an actual result.
	 */
	public TResult getResult()
			throws TaskResultException
	{
		if(!isSuccessful())
			throw new TaskResultException("There was no result, as an error occurred during execution of a task.", error);

		return result;
	}

	void setResult(TResult result)
	{
		this.result = result;
	}

	/**
	 * If an exception occurs during a task, it is stored here.
	 * Otherwise, this is null.
	 * (The exception's stack trace has already been preserved)
	 */
	public Throwable getError()
	{
		return error;
	}

	void setError(Throwable error)
	{
		this.error = error;
	}

	/**
	 * Returns true if no exceptions occurred, i.e. the task was successfully completed.
	 */
	public boolean isSuccessful()
	{
		return error == null;
	}
}