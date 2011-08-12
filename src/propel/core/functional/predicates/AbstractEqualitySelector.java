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
 * Superclass of concrete predicate implementations concerning equality, <=, >= comparisons.
 * Not meant to be used directly.
 */
public abstract class AbstractEqualitySelector<T extends Comparable<T>>
		extends AbstractSelector<T>
{
	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	protected AbstractEqualitySelector(T comparator, NullHandling behaviour)
	{
		super(comparator, behaviour);
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws NullPointerException When the generic type parameter is null.
	 */
	protected AbstractEqualitySelector(T comparator, Class<?> genericTypeParameter, NullHandling behaviour)
	{
		super(comparator, genericTypeParameter, behaviour);
	}

	@Override
	public boolean test(T element)
	{
		switch(behaviour)
		{
			case ThrowException:
				if(element == null)
					throw new NullPointerException("element");
				if(comparator == null)
					throw new NullPointerException("comparator");
				break;
			case Safe:
				if(element == null)
				{
					if(comparator == null)
						return true;
					else
						return false;
				}
				else if(comparator == null)
				{
					if(element == null)
						return true;
					else
						return false;
				}
				break;
			default:
				throw new IllegalArgumentException("Unrecognized null handing behaviour: " + behaviour.toString());
		}

		return compare(element);
	}
}
