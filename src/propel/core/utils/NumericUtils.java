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
package propel.core.utils;

import lombok.val;

/**
 * Provides utility functionality for numberic structures
 */
public final class NumericUtils
{
  /**
   * Returns a character range from start (inclusive) to end (exclusive).
   * 
   * @throws IllegalArgumentException When the end is before start
   */
  public static int[] intRange(final int start, final int end)
  {
    int length = end - start;
    if (length < 0)
      throw new IllegalArgumentException("start=" + start + " end=" + end);

    val result = new int[length];

    int index = 0;
    for (int i = start; i < end; i++)
      result[index++] = i;

    return result;
  }

  /**
   * Returns a range from start (inclusive) to end (exclusive)
   * 
   * @throws NullPointerException An argument is null
   * @throws IllegalArgumentException When the end is before start
   */
  public static Integer[] until(final Integer start, final Integer end)
  {
    return ArrayUtils.box(intRange(start.intValue(), end.intValue()));
  }

  /**
   * Returns a range from start (inclusive) to end (inclusive)
   * 
   * @throws NullPointerException An argument is null
   * @throws IllegalArgumentException When the end is before start
   */
  public static Integer[] to(final Integer start, final Integer end)
  {
    return ArrayUtils.box(intRange(start.intValue(), end.intValue() + 1));
  }

  /**
   * Returns a character range from start (inclusive) to end (exclusive).
   * 
   * @throws IllegalArgumentException When the end is before start, or (end-start) is larger than Integer.MAX_VALUE
   */
  public static long[] longRange(final long start, final long end)
  {
    long length = end - start;
    if (length < 0)
      throw new IllegalArgumentException("start=" + start + " end=" + end);
    if (length > Integer.MAX_VALUE)
      throw new IllegalArgumentException("start=" + start + " end=" + end + " length=" + length);

    val result = new long[(int) length];

    int index = 0;
    for (long i = start; i < end; i++)
      result[index++] = i;

    return result;
  }

  /**
   * Returns a range from start (inclusive) to end (exclusive)
   * 
   * @throws NullPointerException An argument is null
   * @throws IllegalArgumentException When the end is before start
   */
  public static Long[] until(final Long start, final Long end)
  {
    return ArrayUtils.box(longRange(start.longValue(), end.longValue()));
  }

  /**
   * Returns a range from start (inclusive) to end (inclusive)
   * 
   * @throws NullPointerException An argument is null
   * @throws IllegalArgumentException When the end is before start
   */
  public static Long[] to(final Long start, final Long end)
  {
    return ArrayUtils.box(longRange(start.longValue(), end.longValue() + 1));
  }

  private NumericUtils()
  {
  }
}
