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
package propel.core.validation.propertyMetadata;

import propel.core.utils.Linq;
import propel.core.validation.ValidationException;

/**
 * Class aiding in validation of Iterable collections.
 */
public class CollectionPropertyMetadata<T>
		extends ArrayPropertyMetadata
{
	/**
	 * Default constructor
	 */
	protected CollectionPropertyMetadata()
	{
	}

	/**
	 * Initializes with the property name and a pair of a min and max sizes (inclusive)
	 *
	 * @throws IllegalArgumentException An argument is invalid
	 */
	public CollectionPropertyMetadata(String name, int minSize, int maxSize)
	{
		this(name, minSize, maxSize, true);
	}

	/**
	 * Initializes with the property name and a pair of a min and max sizes (inclusive)
	 *
	 * @throws IllegalArgumentException An argument is invalid
	 */
	public CollectionPropertyMetadata(String name, int minSize, int maxSize, boolean notNull)
	{
		super(name, minSize, maxSize, notNull);
	}

	@Override
	protected void checkBounds(Object obj)
			throws ValidationException
	{
		Iterable<T> value = (Iterable<T>) obj;

		int size = Linq.count(value);

		// check conditions
		if(size > getMaxSize())
			throw new ValidationException(String.format(SHOULD_NOT_BE_GREATER_THAN, getName()) + getMaxSize());

		if(size < getMinSize())
			throw new ValidationException(String.format(SHOULD_NOT_BE_LESS_THAN, getName()) + getMinSize());
	}
}
