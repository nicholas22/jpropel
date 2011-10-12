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
package propel.core.tracing;

import java.lang.reflect.Method;
import propel.core.common.CONSTANT;
import propel.core.utils.StringUtils;

/**
 * A simple trace message generator
 */
public class SimpleTraceMessageGenerator
    implements ITraceMessageGenerator
{
  /**
   * {@inheritDoc}
   */
  @Override
  public String argumentsToString(Method method, Object[] args)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(method.getName());
    sb.append(CONSTANT.OPEN_PARENTHESIS);

    // arguments
    if (args != null)
    {
      String[] strArgs = new String[args.length];
      for (int i = 0; i < args.length; i++)
        strArgs[i] = args[i] != null ? args[i].toString() : getNullRepresentationValue();
      sb.append(StringUtils.delimit(strArgs, getArgSeparatorValue()));
    }

    sb.append(CONSTANT.CLOSE_PARENTHESIS);
    return sb.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resultToString(Method method, Object result)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(method.getName());
    sb.append(getReturnedValue());
    sb.append(result != null ? result.toString() : getNullRepresentationValue());
    return sb.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String exceptionToString(Method method, Throwable exception)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(method.getName());
    sb.append(getHasThrownValue(exception));
    sb.append(exception != null ? exception.getClass().getSimpleName() : getNullRepresentationValue());
    return sb.toString();
  }

  protected String getNullRepresentationValue()
  {
    return "[null]";
  }

  protected String getArgSeparatorValue()
  {
    return ", ";
  }

  protected String getReturnedValue()
  {
    return " returned ";
  }

  protected String getHasThrownValue(Throwable e)
  {
    return " has thrown ";
  }

}
