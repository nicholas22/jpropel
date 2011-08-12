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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import propel.core.utils.ReflectionUtils;

/**
 * Class allowing for method call tracing. Note: The generic type parameter T should be an interface that the object you are proxying
 * implements.
 */
public class Tracer<T>
    implements InvocationHandler
{
  // logger used to perform tracing
  private final Logger logger;
  // used for creating the dumped text
  private final ITraceMessageGenerator generator;
  // remember log levels of traced methods
  private final Map<Method, Trace> tracedMethods;
  // used for making actual calls
  private Object originalObject;

  /**
   * Initialises with a logger and a default trace message generator
   */
  public Tracer(Logger logger)
  {
    this(logger, new SimpleTraceMessageGenerator());
  }

  /**
   * Initialises with a logger
   */
  public Tracer(Logger logger, ITraceMessageGenerator generator)
  {
    this.logger = logger;
    this.generator = generator;
    this.tracedMethods = new TreeMap<Method, Trace>(new MethodComparator());
  }

  /**
   * Proxies the given object's public methods. Should only be used to proxy one run-time object. Note that the object must implement some
   * interface(s), which you can use when creating this tracer. It will be used to cast the result to an interface.
   * 
   * @throws NullPointerException An argument is null
   * @throws IllegalStateException This method has already been called
   */
  public T proxy(T objToProxy)
  {
    if (objToProxy == null)
      throw new NullPointerException("objToProxy");
    if (originalObject != null)
      throw new IllegalStateException("This method should only be called once.");
    originalObject = objToProxy;

    // discover all annotated methods
    Class<?> clazz = objToProxy.getClass();

    for (Method method : ReflectionUtils.getMethods(clazz, true))
      // find relevant annotations
      for (Annotation anno : ReflectionUtils.getMethodAnnotations(method, true))
        if (anno instanceof Trace)
        {
          // use required
          Trace trace = (Trace) anno;
          tracedMethods.put(method, trace);
        }

    // proxy and return
    Object result = Proxy.newProxyInstance(objToProxy.getClass().getClassLoader(), ReflectionUtils.getInterfaces(objToProxy.getClass(), true), this);
    try
    {
      return (T) result;
    }
    catch(ClassCastException e)
    {
      throw new IllegalArgumentException(
          "The generic type parameter of the tracer is not an interface type, or the given object does not implement the interface given.",
          e);
    }
  }

  /**
   * Should not be used by client code. Implicitly invoked when method calls are made on the proxied object. Responsible for performing
   * tracing using specified log level.
   */
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable
  {
    Trace traceOptions = null;
    Object result = null;

    try
    {
      // check if we need to dump arguments
      traceOptions = tracedMethods.get(method);

      if (traceOptions != null)
        if (traceOptions.traceArgs())
        {
          // create text to dump
          String arguments = generator.argumentsToString(method, args);
          logWithLevel(traceOptions.level(), arguments);
        }

      // method call here
      result = method.invoke(originalObject, args);

      // dump return object if required (and available)
      if (traceOptions != null)
        if (traceOptions.traceResult())
          if (!ReflectionUtils.isReturnTypeVoid(method))
          {
            // create text to dump
            String ret = generator.resultToString(method, result);
            logWithLevel(traceOptions.level(), ret);
          }
    }
    catch(InvocationTargetException e)
    {
      if (traceOptions != null)
        if (traceOptions.traceExceptions())
        {
          // create text to dump
          String exception = generator.exceptionToString(method, e.getTargetException());
          logWithLevel(traceOptions.level(), exception);
        }

      // get rid of invocation exception, throw actual cause
      throw e.getTargetException();
    }

    return result;
  }

  /**
   * Logs a message using the specified log level
   */
  protected void logWithLevel(LogLevel level, String msg)
  {
    switch(level)
    {
      case TRACE:
        logger.trace(msg);
        break;
      case DEBUG:
        logger.debug(msg);
        break;
      case INFO:
        logger.info(msg);
        break;
      case WARN:
        logger.warn(msg);
        break;
      case ERROR:
        logger.error(msg);
        break;
      default:
        throw new IllegalArgumentException("This trace log level is not recognised: " + level);
    }
  }

}
