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
package propel.core.functional;

import propel.core.utils.SuperTypeToken;

/**
 * Encapsulates a method that defines a set of criteria and determines whether the specified object meets those criteria.
 */
public abstract class Predicate<T>
{
	private final Class<?> genericTypeParameter;

	public Predicate()
	{
		genericTypeParameter = SuperTypeToken.getClazz(this.getClass());
	}

	public Predicate(Class<?> genericTypeParameter)
	{
		if(genericTypeParameter == null)
			throw new NullPointerException("genericTypeParameter");

		this.genericTypeParameter = genericTypeParameter;
	}

	/**
	 * Returns true if the element meets certain criteria.
	 */
	public abstract boolean test(T element);

	/**
	 * Returns generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericTypeParameter()
	{
		return genericTypeParameter;
	}
}
