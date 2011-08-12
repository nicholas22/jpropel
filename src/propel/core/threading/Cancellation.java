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

import propel.core.common.CONSTANT;

/**
 * Class allows for controlling cancellation of a CancellableTask.
 */
public class Cancellation
		implements ICancellation
{
	/**
	 * The default polling interval for checking whether a cancellation has occurred.
	 */
	public final long DEFAULT_POLLING_INTERVAL_MILLIS = 350; // ms
	/**
	 * Whether cancelled or not.
	 */
	private boolean isCancelled;
	/**
	 * The reason for the cancellation
	 */
	private String reason;
	/**
	 * The cancellation source
	 */
	private String source;
	/**
	 * The polling inteval for checking whether isCancelled is true
	 */
	private long pollingIntervalMillis;

	/**
	 * Default constructor
	 */
	public Cancellation()
	{
		this.pollingIntervalMillis = DEFAULT_POLLING_INTERVAL_MILLIS;
		this.source = CONSTANT.EMPTY_STRING;
		this.reason = CONSTANT.EMPTY_STRING;
	}

	/**
	 * Overloaded constructor, allows for specifying the cancellation variable polling interval.
	 *
	 * @throws IllegalArgumentException An argument is out of range
	 */
	public Cancellation(long pollingIntervalMillis)
	{
		this();
		if(pollingIntervalMillis <= 0)
			throw new IllegalArgumentException("pollingIntervalMillis");
		this.pollingIntervalMillis = pollingIntervalMillis;
	}

	public boolean isCancelled()
	{
		return isCancelled;
	}

	public String getReason()
	{
		return reason;
	}

	;

	public String getSource()
	{
		return source;
	}

	public long getPollingIntervalMillis()
	{
		return pollingIntervalMillis;
	}

	/**
	 * Sets status to cancelled.
	 */
	public void cancel()
	{
		isCancelled = true;
	}

	/**
	 * Sets status to cancelled. Allows for provision of a reason.
	 */
	public void cancel(String reason)
	{
		this.reason = reason;
		isCancelled = true;
	}

	/**
	 * Sets status to cancelled. Allows for provision of a reason and a source.
	 */
	public void cancel(String reason, String source)
	{
		this.reason = reason;
		this.source = source;
		isCancelled = true;
	}
}
