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

import propel.core.utils.SuperTypeTokenException;

/**
 * Predicate that is true for elements that are greater than or equal to a comparator.
 */
public class GreaterOrEqualSelector<T extends Comparable<T>>
		extends AbstractEqualitySelector<T>
{
	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	public GreaterOrEqualSelector(T comparator)
	{
		this(comparator, NullHandling.ThrowException);
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws NullPointerException When the generic type parameter is null.
	 */
	public GreaterOrEqualSelector(T comparator, Class<?> genericTypeParameter)
	{
		this(comparator, genericTypeParameter, NullHandling.ThrowException);
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	public GreaterOrEqualSelector(T comparator, NullHandling behaviour)
	{
		super(comparator, behaviour);
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws NullPointerException When the generic type parameter is null.
	 */
	public GreaterOrEqualSelector(T comparator, Class<?> genericTypeParameter, NullHandling behaviour)
	{
		super(comparator, genericTypeParameter, behaviour);
	}

	@Override
	protected boolean compare(T element)
	{
		return comparator.compareTo(element) <= 0;
	}
}
