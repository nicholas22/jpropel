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
package propel.core.common;

import propel.core.utils.ConversionUtils;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Wraps around a Throwable and can retrieve its stacktrace, use the toString() method for this.
 */
public final class StackTraceLogger
{
  private String stackTrace;

  public StackTraceLogger(Throwable e)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    e.printStackTrace(pw);
    pw.flush();
    stackTrace = ConversionUtils.toString(baos.toByteArray(), CONSTANT.UTF8);
  }

  @Override
  public String toString()
  {
    return stackTrace;
  }
}
