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

import propel.core.collections.KeyValuePair;
import propel.core.functional.FunctionWithOneArgument;
import propel.core.utils.SuperTypeTokenException;

/**
 * Returns the value from a key/value pair.
 * Must be instantiated with anonymous class semantics.
 */
public class ValueSelector<TKey, TValue>
		extends FunctionWithOneArgument<KeyValuePair<TKey, TValue>, TValue>
{
	/**
	 * Default constructor.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	public ValueSelector()
	{
		super();
	}

	/**
	 * Initializes with the generic type parameters used.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public ValueSelector(Class<?> genericTypeParameter, Class<?> genericReturnTypeParameter)
	{
		super(genericTypeParameter, genericReturnTypeParameter);
	}

	@Override
	public TValue operateOn(KeyValuePair<TKey, TValue> arg)
	{
		return arg.getValue();
	}
}
