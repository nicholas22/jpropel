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
 * Returns the key from a key/value pair.
 */
public class KeySelector<TKey, TValue>
		extends FunctionWithOneArgument<KeyValuePair<TKey, TValue>, TKey>
{
	/**
	 * Default constructor.
	 *
	 * @throws SuperTypeTokenException When not instantiated using anonymous class semantics.
	 */
	public KeySelector()
	{
		super();
	}

	/**
	 * Initializes with the generic type parameters used.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public KeySelector(Class<?> genericTypeParameter, Class<?> genericReturnTypeParameter)
	{
		super(genericTypeParameter, genericReturnTypeParameter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TKey operateOn(KeyValuePair<TKey, TValue> arg)
	{
		return arg.getKey();
	}
}
