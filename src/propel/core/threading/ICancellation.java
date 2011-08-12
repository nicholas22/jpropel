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
 * Interface of something that can be cancelled, along with cancellation parameters.
 */
public interface ICancellation
{
	/**
	 * Whether cancelled or not.
	 */
	boolean isCancelled();

	/**
	 * The reason for the cancellation
	 */
	String getReason();

	/**
	 * The cancellation source
	 */
	String getSource();

	/**
	 * The polling inteval for checking whether IsCancelled is true
	 */
	long getPollingIntervalMillis();

	/**
	 * Sets status to cancelled.
	 */
	void cancel();

	/**
	 * Sets status to cancelled. Allows for provision of a reason.
	 */
	void cancel(String reason);

	/**
	 * Sets status to cancelled. Allows for provision of a reason and a source.
	 */
	void cancel(String reason, String source);
}
