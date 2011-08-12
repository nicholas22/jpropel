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

import propel.core.collections.arrays.ReifiedArray;
import propel.core.validation.ValidationException;

/**
 * Class aiding in validation of arrays.
 */
public class ArrayPropertyMetadata
		extends NullablePropertyMetadata<Object>
{
	/**
	 * Error message when a maximum value is less than the minimum value
	 */
	public static final String PROPERTY_ERROR_MAX_LESS_THAN_MIN = "%s maximum size cannot be less than the allowed minimum size!";
	/**
	 * Error message when a size is too high
	 */
	public static final String SHOULD_NOT_BE_GREATER_THAN = "%s should not have a size larger than ";
	/**
	 * Error message when a size is too low
	 */
	public static final String SHOULD_NOT_BE_LESS_THAN = "%s should not have a size smaller than than ";
	/**
	 * Error message when a size is negative
	 */
	public static final String SHOULD_NOT_BE_NEGATIVE = "%s bound cannot be negative!";
	/**
	 * The minimum inclusive value allowed
	 */
	private int minSize;
	/**
	 * The maximum inclusive value allowed
	 */
	private int maxSize;

	/**
	 * Default constructor
	 */
	protected ArrayPropertyMetadata()
	{
	}

	/**
	 * Initializes with the property name and a pair of a min and max sizes (inclusive)
	 *
	 * @throws IllegalArgumentException An argument is invalid
	 */
	public ArrayPropertyMetadata(String name, int minSize, int maxSize)
	{
		this(name, minSize, maxSize, true);
	}

	/**
	 * Initializes with the property name and a pair of a min and max sizes (inclusive)
	 *
	 * @throws IllegalArgumentException An argument is invalid
	 */
	public ArrayPropertyMetadata(String name, int minSize, int maxSize, boolean notNull)
	{
		super(name, notNull);

		if(minSize < 0)
			throw new IllegalArgumentException(String.format(SHOULD_NOT_BE_NEGATIVE, "minSize"));
		if(maxSize < 0)
			throw new IllegalArgumentException(String.format(SHOULD_NOT_BE_NEGATIVE, "maxSize"));

		this.minSize = minSize;
		this.maxSize = maxSize;
		if(minSize > maxSize)
			throw new IllegalArgumentException(String.format(PROPERTY_ERROR_MAX_LESS_THAN_MIN, name));
	}

	public int getMinSize()
	{
		return minSize;
	}

	public void setMinSize(int minSize)
	{
		this.minSize = minSize;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object validate(Object value)
			throws ValidationException
	{
		super.validate(value);

		// only check bounds if not null
		if(value != null)
			checkBounds(value);

		return value;
	}

	protected void checkBounds(Object obj)
			throws ValidationException
	{
		ReifiedArray<Object> value = new ReifiedArray<Object>(obj);

		// check conditions
		if(value.length() > getMaxSize())
			throw new ValidationException(String.format(SHOULD_NOT_BE_GREATER_THAN, getName()) + getMaxSize());

		if(value.length() < getMinSize())
			throw new ValidationException(String.format(SHOULD_NOT_BE_LESS_THAN, getName()) + getMinSize());
	}
}
