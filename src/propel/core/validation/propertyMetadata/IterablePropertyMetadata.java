// /////////////////////////////////////////////////////////
// This file is part of Propel.
//
// Propel is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Propel is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with Propel. If not, see <http://www.gnu.org/licenses/>.
// /////////////////////////////////////////////////////////
// Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
// /////////////////////////////////////////////////////////
package propel.core.validation.propertyMetadata;

import propel.core.collections.arrays.ReifiedArray;
import propel.core.utils.Linq;
import propel.core.validation.ValidationException;

/**
 * Class aiding in validation of Iterables.
 */
public class IterablePropertyMetadata<T>
    extends ArrayPropertyMetadata
{
  /**
   * Default constructor
   */
  protected IterablePropertyMetadata()
  {
  }

  /**
   * Initializes with the property name and a pair of a min and max sizes (inclusive)
   * 
   * @throws IllegalArgumentException An argument is invalid
   */
  public IterablePropertyMetadata(String name, int minSize, int maxSize, boolean notNull, boolean noNullElements)
  {
    super(name, minSize, maxSize, notNull, noNullElements);
  }

  @Override
  protected void checkBounds(ReifiedArray<Object> array)
      throws ValidationException
  {
    @SuppressWarnings("unchecked")
    Iterable<T> value = (Iterable<T>) array;

    int size = Linq.count(value);

    if (getMaxSize() == getMinSize())
      if (size != getMaxSize())
        throw new ValidationException(String.format(SHOULD_BE_EXACTLY, getName()) + getMaxSize());

    // check conditions
    if (size > getMaxSize())
      throw new ValidationException(String.format(SHOULD_NOT_BE_GREATER_THAN, getName()) + getMaxSize());

    if (size < getMinSize())
      throw new ValidationException(String.format(SHOULD_NOT_BE_LESS_THAN, getName()) + getMinSize());
  }
}
