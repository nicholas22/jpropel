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
package propel.core.functional.math;

import propel.core.functional.FunctionWithOneArgument;

/**
 * Function that can be used to provide multiples of a specific value.
 */
public final class MultiplyIntFunction
		extends FunctionWithOneArgument<Integer, Integer>
{
	private final int multiplier;

	/**
	 * Constructor initializes with the multiplier value
	 */
	public MultiplyIntFunction(int multiplier)
	{
		super(Integer.class, Integer.class);
		this.multiplier = multiplier;
	}

	@Override
	public Integer operateOn(Integer arg)
	{
		return arg.intValue() * multiplier;
	}
}
