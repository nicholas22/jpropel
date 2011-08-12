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
package propel.core.functional.predicates;

import propel.core.functional.Predicate;

/**
 * Used as a predicate for filtering out non-null values e.g. str => str == null
 */
public class NullSelector<T>
		extends Predicate<T>
{
	/**
	 * Default constructor
	 *
	 * @throws propel.core.utils.SuperTypeTokenException
	 *          When not instantiated using anonymous class semantics.
	 */
	public NullSelector()
	{
		super();
	}

	/**
	 * Initializes with the generic type parameter
	 *
	 * @throws NullPointerException When the generic type parameter is null.
	 */
	public NullSelector(Class<?> genericTypeParameter)
	{
		super(genericTypeParameter);
	}

	@Override
	public boolean test(T arg)
	{
		return arg == null;
	}
}
