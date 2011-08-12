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
 * This encapsulates a generic action that uses one argument.
 */
public abstract class ActionWithOneArgument<T>
{
	private final Class<?> genericTypeParameter;

	public ActionWithOneArgument()
	{
		genericTypeParameter = SuperTypeToken.getClazz(this.getClass(), 0);
	}

	public ActionWithOneArgument(Class<?> genericTypeParameter)
	{
		if(genericTypeParameter == null)
			throw new NullPointerException("genericTypeParameter");

		this.genericTypeParameter = genericTypeParameter;
	}

	/**
	 * Execute the action
	 */
	public abstract void executeWith(T arg);

	/**
	 * Returns the generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericTypeParameter()
	{
		return genericTypeParameter;
	}
}
