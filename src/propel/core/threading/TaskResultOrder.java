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
 * The order that tasks results are to be returned
 */
public enum TaskResultOrder
{
	/**
	 * Task results have no order
	 */
	None(0),
	/**
	 * Task results are returned in the same order as the tasks were added
	 */
	Ordered(1),
	/**
	 * Reverse order to Ordered.
	 */
	ReverseOrder(2);

	// private
	private int order;

	private TaskResultOrder(int order)
	{
		this.order = order;
		if(order < 0 || order > 2)
			throw new IllegalArgumentException("order=" + order);
	}

	private int getOrder()
	{
		return order;
	}
}
