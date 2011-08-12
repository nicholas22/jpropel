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
package propel.core.tracing;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * A simple method comparator, using the methods' hash code for comparison.
 */
class MethodComparator
    implements Comparator<Method>
{

  @Override
  public int compare(Method arg0, Method arg1)
  {
    // name comparison
    int comparison = arg0.getName().compareTo(arg1.getName());
    if (comparison != 0)
      return comparison;

    // return type comparison
    comparison = arg0.getReturnType().getName().compareTo(arg1.getReturnType().getName());
    if (comparison != 0)
      return comparison;

    // arg length comparison
    comparison = Integer.valueOf(arg0.getParameterTypes().length).compareTo(Integer.valueOf(arg1.getParameterTypes().length));
    if (comparison != 0)
      return comparison;

    // arg type comparison
    for (int i = 0; i < arg0.getParameterTypes().length; i++)
    {
      comparison = arg0.getParameterTypes()[i].getName().compareTo(arg1.getParameterTypes()[i].getName());
      if (comparison != 0)
        return comparison;
    }

    // methods are referring to the same method (could be on different type, but that's OK)
    return 0;
  }
}
