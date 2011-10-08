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
package propel.core.functional.predicates;

import lombok.Function;
import propel.core.utils.StringUtils;

/**
 * Some common, re-usable predicates
 */
public final class Predicates
{
  private Predicates()
  {
  }

  @Function
  public static <T> Boolean equal(T element, T _value)
  {
    if (element == null)
    {
      if (_value == null)
        return true;
      else
        return false;
    } else
    {
      if (_value == null)
        return false;
      else
        return element.equals(_value);
    }
  }

  @Function
  public static <T> Boolean notEqual(T element, T _value)
  {
    if (element == null)
    {
      if (_value == null)
        return false;
      else
        return true;
    } else
    {
      if (_value == null)
        return true;
      else
        return !element.equals(_value);
    }
  }

  @Function
  public static <T extends Comparable<T>> Boolean greaterThan(T element, T _value)
  {
    return element.compareTo(_value) > 0;
  }

  @Function
  public static <T extends Comparable<T>> Boolean lessThan(T element, T _value)
  {
    return element.compareTo(_value) < 0;
  }

  @Function
  public static <T extends Comparable<T>> Boolean greaterThanOrEqual(T element, T _value)
  {
    return element.compareTo(_value) >= 0;
  }

  @Function
  public static <T extends Comparable<T>> Boolean lessThanOrEqual(T element, T _value)
  {
    return element.compareTo(_value) <= 0;
  }

  @Function
  public static <T> Boolean isNull(T element)
  {
    return element == null;
  }

  @Function
  public static <T> Boolean isNotNull(T element)
  {
    return element != null;
  }

  @Function
  public static Boolean isEmpty(String element)
  {
    return element.isEmpty();
  }

  @Function
  public static Boolean isNotEmpty(String element)
  {
    return !element.isEmpty();
  }

  @Function
  public static Boolean isNullOrEmpty(String element)
  {
    return StringUtils.isNullOrEmpty(element);
  }

  @Function
  public static Boolean isNotNullOrEmpty(String element)
  {
    return !StringUtils.isNullOrEmpty(element);
  }
  
  @Function
  public static Boolean isNullOrBlank(String element)
  {
    return StringUtils.isNullOrBlank(element);
  }

  @Function
  public static Boolean isNotNullOrBlank(String element)
  {
    return !StringUtils.isNullOrBlank(element);
  }
  
}
