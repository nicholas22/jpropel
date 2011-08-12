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

import propel.core.functional.FunctionWithOneArgument;

import java.util.Locale;

/**
 * Used as a null-coalescing string converter function i.e. x=> x != null ? x.toLowerCase() : null
 */
public final class StringToLowercaseConverter
		extends FunctionWithOneArgument<String, String>
{
	private final Locale locale;

	public StringToLowercaseConverter()
	{
		this(null);
	}

	public StringToLowercaseConverter(Locale locale)
	{
		super(String.class, String.class);
		this.locale = locale;
	}

	@Override
	public String operateOn(String arg)
	{
		if(locale == null)
			return arg != null ? arg.toLowerCase() : null;
		else
			return arg != null ? arg.toLowerCase(locale) : null;
	}
}
