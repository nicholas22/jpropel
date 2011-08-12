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
package propel.core.observer;

/**
 * Specifies a behaviour upon failure when notifying an observer of a collection of an addition/removal event.
 * Observers should ideally wrap the entire addition/removal event handler bodies in try/catch blocks, but this is not guaranteed to have happened
 * and this is where this behaviour comes into play.
 */
public enum ObserverFailureHandlingMode
{
	/**
	 * Upon a fault, the exception that occurs is thrown
	 */
	ThrowOnError(0),
	/**
	 * Upon a fault, the error is handled by removing the observer from the collection of observers; it therefore does not receive any further notifications.
	 */
	RemoveObserver(1),
	/**
	 * Upon a fault, the exception occurring upon notification is swallowed (i.e. not thrown) and notifications continue with next observer
	 */
	IgnoreErrors(2);

	// private
	private int mode;

	// constructor
	private ObserverFailureHandlingMode(int mode)
	{
		this.mode = mode;
		if(mode < 0 || mode > 2)
			throw new IllegalArgumentException("mode=" + mode);
	}

	public int getMode()
	{
		return mode;
	}

}
