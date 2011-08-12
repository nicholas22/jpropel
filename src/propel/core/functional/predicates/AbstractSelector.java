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
import propel.core.utils.SuperTypeTokenException;

/**
 * Superclass of concrete predicate implementations concerning equality, inequality, <, >, <=, >= comparisons.
 * Not meant to be used directly.
 */
public abstract class AbstractSelector<T extends Comparable<T>>
		extends Predicate<T>
{
	protected final T comparator;
	protected final NullHandling behaviour;

	private AbstractSelector()
	{
		comparator = null;
		behaviour = NullHandling.ThrowException;
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	protected AbstractSelector(T comparator, NullHandling behaviour)
	{
		this.comparator = comparator;
		switch(behaviour)
		{
			case Safe:
			case ThrowException:
				this.behaviour = behaviour;
				break;
			default:
				throw new IllegalArgumentException("Unrecognized null handing behaviour: " + behaviour.toString());
		}
	}

	/**
	 * Initializes with the element that will be used for comparisons.
	 *
	 * @throws NullPointerException When the generic type parameter is null.
	 */
	protected AbstractSelector(T comparator, Class<?> genericTypeParameter, NullHandling behaviour)
	{
		this(comparator, behaviour);

		if(genericTypeParameter == null)
			throw new NullPointerException("genericTypeParameter");
	}

	/**
	 * Returns true if the predicate is true for the given element
	 */
	public abstract boolean test(T element);

	/**
	 * Performs the actual comparator comparison
	 *
	 * @return True if the predicate is true, false otherwise.
	 */
	protected abstract boolean compare(T element);

}
