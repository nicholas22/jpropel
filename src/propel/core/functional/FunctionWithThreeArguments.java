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
 * This encapsulates a generic function that accepts three arguments
 */
public abstract class FunctionWithThreeArguments<T1, T2, T3, TResult>
{
	private final Class<?> genericTypeParameter1;
	private final Class<?> genericTypeParameter2;
	private final Class<?> genericTypeParameter3;
	private final Class<?> genericReturnTypeParameter;

	public FunctionWithThreeArguments()
	{
		genericTypeParameter1 = SuperTypeToken.getClazz(this.getClass(), 0);
		genericTypeParameter2 = SuperTypeToken.getClazz(this.getClass(), 1);
		genericTypeParameter3 = SuperTypeToken.getClazz(this.getClass(), 2);
		genericReturnTypeParameter = SuperTypeToken.getClazz(this.getClass(), 3);
	}

	public FunctionWithThreeArguments(Class<?> genericTypeParameter1, Class<?> genericTypeParameter2, Class<?> genericTypeParameter3, Class<?> genericReturnTypeParameter)
	{
		if(genericTypeParameter1 == null)
			throw new NullPointerException("genericTypeParameter1");
		if(genericTypeParameter2 == null)
			throw new NullPointerException("genericTypeParameter2");
		if(genericTypeParameter3 == null)
			throw new NullPointerException("genericTypeParameter3");
		if(genericReturnTypeParameter == null)
			throw new NullPointerException("genericReturnTypeParameter");

		this.genericTypeParameter1 = genericTypeParameter1;
		this.genericTypeParameter2 = genericTypeParameter2;
		this.genericTypeParameter3 = genericTypeParameter3;
		this.genericReturnTypeParameter = genericReturnTypeParameter;
	}

	/**
	 * Execute the function
	 */
	public abstract TResult operateOn(T1 arg1, T2 arg2, T3 arg3);

	/**
	 * Returns the first generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericTypeParameter1()
	{
		return genericTypeParameter1;
	}

	/**
	 * Returns the second generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericTypeParameter2()
	{
		return genericTypeParameter2;
	}

	/**
	 * Returns the third generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericTypeParameter3()
	{
		return genericTypeParameter3;
	}

	/**
	 * Returns the fourth generic type parameter used to initialize this class.
	 */
	public Class<?> getGenericReturnTypeParameter()
	{
		return genericReturnTypeParameter;
	}
}

