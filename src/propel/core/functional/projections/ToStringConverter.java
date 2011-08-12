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
package propel.core.functional.projections;

import propel.core.common.CONSTANT;
import propel.core.functional.FunctionWithOneArgument;

/**
 * Calls the toString() method on all objects. If an object is null, then a value specified is used, which is "" by default.
 */
public class ToStringConverter<T>
		extends FunctionWithOneArgument<T, String>
{
	/**
	 * This string value is used when an element is null and as such toString() cannot be called.
	 */
	private String nullReplacementValue;

	/**
	 * Default constructor.
	 *
	 * @throws propel.core.utils.SuperTypeTokenException
	 *          When not instantiated using anonymous class semantics.
	 */
	public ToStringConverter()
	{
		super();
		nullReplacementValue = CONSTANT.EMPTY_STRING;
	}

	/**
	 * Constructor uses a value when the toString() method cannot be called because an element is null.
	 *
	 * @throws propel.core.utils.SuperTypeTokenException
	 *          When not instantiated using anonymous class semantics.
	 */
	public ToStringConverter(String nullReplacementValue)
	{
		super();
		nullReplacementValue = CONSTANT.EMPTY_STRING;
	}

	/**
	 * Initializes with the generic type parameters used.
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public ToStringConverter(Class<?> genericTypeParameter)
	{
		super(genericTypeParameter, String.class);
	}

	/**
	 * Initializes with the generic type parameters used.
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public ToStringConverter(Class<?> genericTypeParameter, String nullReplacementValue)
	{
		super(genericTypeParameter, String.class);
		this.nullReplacementValue = nullReplacementValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String operateOn(T arg)
	{
		return arg != null ? arg.toString() : CONSTANT.EMPTY_STRING;
	}
}