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
package propel.core.utils;

import propel.core.collections.ReifiedIterable;
import propel.core.collections.lists.ReifiedArrayList;
import propel.core.collections.lists.ReifiedList;
import propel.core.collections.maps.ReifiedMap;
import propel.core.collections.maps.avl.AvlHashtable;
import propel.core.configuration.ConfigurableConsts;
import propel.core.configuration.ConfigurableParameters;
import propel.core.functional.FunctionWithOneArgument;
import propel.core.functional.FunctionWithTwoArguments;
import propel.core.functional.Predicate;
import propel.core.functional.projections.ArgumentToResultConverter;

import java.lang.reflect.Array;
import java.util.*;

// TODO: test unused methods

/**
 * Provides similar functionality to .NET language integrated queries (LINQ).
 * There are usually two versions of each function, one for operating on Iterables (such as collections) and one with arrays.
 * This is because in Java, arrays are subclasses of Object and not of Iterable, Collection, List, etc.
 */
public final class Linq
{
	/**
	 * The default list size to use for collecting results when the result size is unknown
	 */
	public static final int DEFAULT_LIST_SIZE = ConfigurableParameters.getInt32(ConfigurableConsts.LINQ_DEFAULT_LIST_SIZE);

	/**
	 * Private constructor prevents instantiation.
	 */
	private Linq()
	{
	}

	/**
	 * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <TSource, TAccumulate> TAccumulate aggregate(Iterable<TSource> values, TAccumulate seed, FunctionWithTwoArguments<TAccumulate, ? super TSource, TAccumulate> function)
	{
		return aggregate(values, seed, function, new ArgumentToResultConverter<TAccumulate, TAccumulate>(function.getGenericTypeParameter1(), function.getGenericReturnTypeParameter())
		{
		});
	}

	/**
	 * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <TSource, TAccumulate> TAccumulate aggregate(TSource[] values, TAccumulate seed, FunctionWithTwoArguments<TAccumulate, ? super TSource, TAccumulate> function)
	{
		return aggregate(values, seed, function, new ArgumentToResultConverter<TAccumulate, TAccumulate>(function.getGenericTypeParameter1(), function.getGenericReturnTypeParameter())
		{
		});
	}

	/**
	 * Applies an accumulator function over a sequence. The specified  seed value is used as the initial accumulator value, and the
	 * specified function is used to select the result value from the accumulator's type.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <TSource, TAccumulate, TResult> TResult aggregate(Iterable<TSource> values, TAccumulate seed, FunctionWithTwoArguments<TAccumulate, ? super TSource, TAccumulate> function, FunctionWithOneArgument<TAccumulate, TResult> resultSelector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(seed == null)
			throw new NullPointerException("seed");
		if(function == null)
			throw new NullPointerException("function");
		if(resultSelector == null)
			throw new NullPointerException("resultSelector");

		TAccumulate result = seed;

		for(TSource item : values)
			result = function.operateOn(result, item);

		return resultSelector.operateOn(result);
	}

	/**
	 * Applies an accumulator function over a sequence. The specified  seed value is used as the initial accumulator value, and the
	 * specified function is used to select the result value from the accumulator's type.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <TSource, TAccumulate, TResult> TResult aggregate(TSource[] values, TAccumulate seed, FunctionWithTwoArguments<TAccumulate, ? super TSource, TAccumulate> function, FunctionWithOneArgument<TAccumulate, TResult> resultSelector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(seed == null)
			throw new NullPointerException("seed");
		if(function == null)
			throw new NullPointerException("function");
		if(resultSelector == null)
			throw new NullPointerException("resultSelector");

		TAccumulate result = seed;

		for(int i = 0; i < values.length; i++)
			result = function.operateOn(result, values[i]);

		return resultSelector.operateOn(result);
	}

	/**
	 * Returns true if a condition is true for all items in a sequence.
	 * Otherwise returns false.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean all(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		for(T val : values)
			if(!predicate.test(val))
				return false;

		return true;
	}

	/**
	 * Returns true if a condition is true for all items in a sequence.
	 * Otherwise returns false.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean all(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		for(int i = 0; i < values.length; i++)
			if(!predicate.test(values[i]))
				return false;

		return true;
	}

	/**
	 * Returns true if a condition is true for any of the items in a sequence.
	 * Otherwise returns false.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> boolean any(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		for(T val : values)
			if(predicate.test(val))
				return true;

		return false;
	}

	/**
	 * Returns true if a condition is true for any of the items in a sequence.
	 * Otherwise returns false.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> boolean any(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		int count = values.length;
		for(int i = 0; i < count; i++)
			if(predicate.test(values[i]))
				return true;

		return false;
	}

	/**
	 * Casts a sequence of values of a certain type to a sequence of values of another type.
	 * Uses InvalidCastBehaviour.Remove i.e. excluding any elements that do not successfully cast, without throwing exceptions.
	 * This operates differently to OfType, in that it forces a cast rather than checking if a TSource is of TDest type.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TSource, TDest> ReifiedList<TDest> cast(Iterable<TSource> values, Class<TDest> destinationClass)
	{
		return cast(values, destinationClass, InvalidCastBehaviour.Remove);
	}

	/**
	 * Casts an array of values of a certain type to an array of values of another type.
	 * Uses InvalidCastBehaviour.Remove i.e. excluding any elements that do not successfully cast, without throwing exceptions.
	 * This operates differently to OfType, in that it forces a cast rather than checking if a TSource is of TDest type.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TSource, TDest> TDest[] cast(TSource[] values, Class<TDest> destClass)
	{
		return (TDest[]) cast(values, destClass, InvalidCastBehaviour.Remove);
	}

	/**
	 * Casts a sequence of values of a certain type to a sequence of values of another type,
	 * using the specified behaviour upon the event of a cast failure.
	 * This operates differently to OfType, in that it forces a cast rather than checking if a TSource is of TDest type.
	 *
	 * @throws NullPointerException	 When an argument is null.
	 * @throws IllegalArgumentException Unrecognized cast behaviour.
	 */
	public static <TSource, TDest> ReifiedList<TDest> cast(Iterable<TSource> values, Class<TDest> destinationClass, InvalidCastBehaviour castBehaviour)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(destinationClass == null)
			throw new NullPointerException("destinationClass");

		switch(castBehaviour)
		{
			case Throw:
				return castThrow(values, destinationClass);
			case Remove:
				return castRemove(values, destinationClass);
			case UseDefault:
				return castUseDefault(values, destinationClass);
			default:
				throw new IllegalArgumentException("Unrecognised cast behaviour: " + castBehaviour);
		}
	}

	/**
	 * Casts an array of values of a certain type to an array of values of another type,
	 * using the specified behaviour upon the event of a cast failure.
	 * This operates differently to OfType, in that it forces a cast rather than checking if a TSource is of TDest type.
	 *
	 * @throws NullPointerException	 When an argument is null.
	 * @throws IllegalArgumentException Unrecognized cast behaviour.
	 */
	public static <TSource, TDest> TDest[] cast(TSource[] values, Class<TDest> destClass, InvalidCastBehaviour castBehaviour)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedArrayList<TDest> list = new ReifiedArrayList<TDest>(values.length, destClass);

		switch(castBehaviour)
		{
			case Throw:
				castThrow(values, list);
				break;
			case Remove:
				castRemove(values, list);
				break;
			case UseDefault:
				castUseDefault(values, list);
				break;
			default:
				throw new IllegalArgumentException("Unrecognised cast behaviour: " + castBehaviour);
		}

		return toArray(list);
	}

	/**
	 * Concatenates two or more sequences
	 *
	 * @throws NullPointerException When the values or one of its elements is null.
	 */
	public static <T> List<T> concat(Iterable<? extends T>... values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(Iterable<? extends T> vals : values)
		{
			if(vals == null)
				throw new NullPointerException("Item of values");

			for(T item : vals)
				result.add(item);
		}

		return result;
	}

	/**
	 * Concatenates two or more sequences
	 *
	 * @throws NullPointerException	 When the values or one of its elements is null.
	 * @throws IllegalArgumentException When the array has no elements
	 */
	public static <T> ReifiedList<T> concat(ReifiedIterable<T>... values)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(values.length <= 0)
			throw new IllegalArgumentException("length=" + values.length);

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values[0].getGenericTypeParameter());

		for(Iterable<T> vals : values)
		{
			if(vals == null)
				throw new NullPointerException("Item of values");

			for(T item : vals)
				result.add(item);
		}

		return result;
	}

	/**
	 * Concatenates two or more arrays
	 *
	 * @throws NullPointerException When the values or one of its elements is null.
	 */
	public static <T> T[] concat(T[]... values)
	{
		if(values == null)
			throw new NullPointerException("values");

		Class<?> componentType = null;
		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T[] vals : values)
		{
			if(vals == null)
				throw new NullPointerException("Item of values");
			if(componentType == null)
				componentType = vals.getClass().getComponentType();

			for(T item : vals)
				result.add(item);
		}

		return toArray(result, componentType);
	}

	/**
	 * Returns true if an item is contained in a sequence.
	 * Scans the sequence linearly. You may scan for nulls.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> boolean contains(Iterable<T> values, T item)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return containsNull(values);
		else
			return containsNonNull(values, item);
	}

	/**
	 * Returns true if an item is contained in a sequence.
	 * Scans the sequence linearly. You may scan for nulls.
	 *
	 * @throws NullPointerException When the values argument or the comparer is null.
	 */
	public static <T> boolean contains(Iterable<T> values, T item, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		if(item == null)
			return containsNull(values);
		else
			return containsNonNull(values, item, comparer);
	}

	/**
	 * Returns true if an item is contained in a sequence.
	 * Scans the sequence linearly. You may scan for nulls.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> boolean contains(T[] values, T item)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return containsNull(values);
		else
			return containsNonNull(values, item);
	}

	/**
	 * Returns true if an item is contained in a sequence.
	 * Scans the sequence linearly. You may scan for nulls.
	 *
	 * @throws NullPointerException When the array or the comparer is null.
	 */
	public static <T> boolean contains(T[] values, T item, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		if(item == null)
			return containsNull(values);
		else
			return containsNonNull(values, item, comparer);
	}

	/**
	 * Returns true if any of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAny(Iterable<T> values, Iterable<T> items)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(contains(values, item))
				return true;

		return false;
	}

	/**
	 * Returns true if any of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAny(Iterable<T> values, Iterable<T> items, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(contains(values, item, comparer))
				return true;

		return false;
	}

	/**
	 * Returns true if any of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAny(T[] values, T[] items)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(contains(values, item))
				return true;

		return false;
	}

	/**
	 * Returns true if any of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAny(T[] values, T[] items, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(contains(values, item, comparer))
				return true;

		return false;
	}

	/**
	 * Returns true if all of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAll(Iterable<T> values, Iterable<T> items)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(!contains(values, item))
				return false;

		return true;
	}

	/**
	 * Returns true if all of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAll(Iterable<T> values, Iterable<T> items, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(!contains(values, item, comparer))
				return false;

		return true;
	}

	/**
	 * Returns true if all of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAll(T[] values, T[] items)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(!contains(values, item))
				return false;

		return true;
	}

	/**
	 * Returns true if all of the items are contained in the given values.
	 * Scans the values sequence linearly, up to items.Length number of times.
	 * You may scan for null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> boolean containsAll(T[] values, T[] items, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(items == null)
			throw new NullPointerException("items");

		for(T item : items)
			if(!contains(values, item, comparer))
				return false;

		return true;
	}

	// TODO: test this works as expected, use breakpoints

	/**
	 * Counts an Iterable in the most efficient manner possible.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> int count(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(values instanceof ReifiedList<?>)
		{
			ReifiedList<?> list = (ReifiedList<?>) values;
			return list.size();
		}
		if(values instanceof ReifiedMap<?, ?>)
		{
			ReifiedMap<?, ?> map = (ReifiedMap<?, ?>) values;
			return map.size();
		}
		if(values instanceof Collection<?>)
		{
			Collection<?> list = (Collection<?>) values;
			return list.size();
		}
		if(values instanceof Map<?, ?>)
		{
			Map<?, ?> map = (Map<?, ?>) values;
			return map.size();
		}

		// resort to counting elements one by one
		int result = 0;
		for(T item : values)
			result++;

		return result;
	}

	/**
	 * Returns the array length.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> int count(T[] array)
	{
		if(array == null)
			throw new NullPointerException("array");

		return array.length;
	}

	/**
	 * Returns the number of occurrences of an object in a sequence. It is possible to search for null objects.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int count(Iterable<T> values, T item)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return countNulls(values);
		else
			return countNonNull(values, item);
	}

	/**
	 * Returns the number of occurrences of an object in a sequence. It is possible to search for null objects.
	 *
	 * @throws NullPointerException When the values argument or the comparer is null.
	 */
	public static <T> int count(Iterable<T> values, T item, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return countNulls(values);
		else
			return countNonNull(values, item, comparer);
	}

	/**
	 * Returns the number of occurrences of an object in a sequence. It is possible to search for null objects.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int count(T[] values, T item)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return countNulls(values);
		else
			return countNonNull(values, item);
	}

	/**
	 * Returns the number of occurrences of an object in a sequence. It is possible to search for null objects.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int count(T[] values, T item, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(item == null)
			return countNulls(values);
		else
			return countNonNull(values, item, comparer);
	}

	/**
	 * Returns the number of occurrences that satisfy the given condition.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> int countWhere(Iterable<? extends T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		int counter = 0;
		for(T item : values)
		{
			if(predicate.test(item))
				counter++;
		}

		return counter;
	}

	/**
	 * Returns the number of occurrences that satisfy the given condition.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> int countWhere(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		int counter = 0;
		for(T item : values)
		{
			if(predicate.test(item))
				counter++;
		}

		return counter;
	}

	/**
	 * Returns the sequence if at least one element exists in it, otherwise returns a collection
	 * consisting of a single element which has a null value.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> List<T> defaultIfEmpty(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			result.add(item);

		if(result.size() == 0)
			result.add(null);

		return result;
	}

	/**
	 * Returns the sequence if at least one element exists in it, otherwise returns a collection
	 * consisting of a single element which has a null value.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> ReifiedList<T> defaultIfEmpty(ReifiedIterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());
		for(T item : values)
			result.add(item);

		if(result.size() == 0)
			result.add(null);

		return result;
	}

	/**
	 * Returns the sequence if at least one element exists in it, otherwise returns a collection
	 * consisting of a single element which has a null value.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T[] defaultIfEmpty(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			result.add(item);

		if(result.size() == 0)
			result.add(null);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> List<T> distinct(Iterable<T> values)
	{
		return distinct(values, null);
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> ReifiedList<T> distinct(ReifiedIterable<T> values)
	{
		return distinct(values, null);
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T[] distinct(T[] values)
	{
		return distinct(values, null);
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence.
	 * Uses the specified comparer to identify duplicates.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> List<T> distinct(Iterable<? extends T> values, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");

		Set<T> set;
		if(comparer != null)
			set = new TreeSet<T>(comparer);
		else
			set = new TreeSet<T>();

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			if(!set.contains(item))
			{
				set.add(item);
				result.add(item);
			}

		return result;
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence.
	 * Uses the specified comparer to identify duplicates.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> ReifiedList<T> distinct(ReifiedIterable<T> values, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");

		Set<T> set;
		if(comparer != null)
			set = new TreeSet<T>(comparer);
		else
			set = new TreeSet<T>();

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());
		for(T item : values)
			if(!set.contains(item))
			{
				set.add(item);
				result.add(item);
			}

		return result;
	}

	/**
	 * Returns distinct (i.e. no duplicate) elements from a sequence.
	 * Uses the specified comparer to identify duplicates.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> T[] distinct(T[] values, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedList<T> list = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getClass().getComponentType());
		Set<T> set;
		if(comparer != null)
			set = new TreeSet<T>(comparer);
		else
			set = new TreeSet<T>();

		for(T item : values)
			if(!set.contains(item))
			{
				set.add(item);
				list.add(item);
			}

		return list.toArray();
	}

	/**
	 * Returns the element at the given position in the provided sequence.
	 *
	 * @throws NullPointerException	  When the values argument is null.
	 * @throws IndexOutOfBoundsException When the index is out of range.
	 */
	public static <T> T elementAt(Iterable<T> values, int index)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(index < 0)
			throw new IndexOutOfBoundsException("index=" + index);

		int i = 0;
		for(T item : values)
		{
			if(i == index)
				return item;
			i++;
		}

		throw new IndexOutOfBoundsException("max=" + i + " index=" + index);
	}

	/**
	 * Returns the element at the given position in the provided sequence.
	 * It will return null if there is no such index.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> T elementAtOrDefault(Iterable<T> values, int index)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(index >= 0)
		{
			int i = 0;
			for(T item : values)
			{
				if(i == index)
					return item;
				i++;
			}
		}

		return null;
	}

	/**
	 * Returns the element at the given position in the provided sequence.
	 * It will return null if there is no such index.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> T elementAtOrDefault(T[] values, int index)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(index >= 0 && index < values.length)
			return values[index];

		return null;
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> List<T> except(Iterable<T> values, Iterable<T> removedValues)
	{
		return except(values, removedValues, null);
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> ReifiedList<T> except(ReifiedIterable<T> values, ReifiedIterable<T> removedValues)
	{
		return except(values, removedValues, null);
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When the values or removedValues argument is null.
	 */
	public static <T> List<T> except(Iterable<T> values, Iterable<T> removedValues, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(removedValues == null)
			throw new NullPointerException("removedValues");

		List<T> distinctValues;
		List<T> distinctRemovedValues;

		if(comparer == null)
		{
			distinctValues = distinct(values);
			distinctRemovedValues = distinct(removedValues);
		}
		else
		{
			distinctValues = distinct(values, comparer);
			distinctRemovedValues = distinct(removedValues, comparer);
		}

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T item : distinctValues)
			if(!contains(distinctRemovedValues, item))
				result.add(item);

		return result;
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When the values or removedValues argument is null.
	 */
	public static <T> ReifiedList<T> except(ReifiedIterable<T> values, ReifiedIterable<T> removedValues, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(removedValues == null)
			throw new NullPointerException("removedValues");

		ReifiedList<T> distinctValues;
		ReifiedList<T> distinctRemovedValues;

		if(comparer == null)
		{
			distinctValues = distinct(values);
			distinctRemovedValues = distinct(removedValues);
		}
		else
		{
			distinctValues = distinct(values, comparer);
			distinctRemovedValues = distinct(removedValues, comparer);
		}

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());

		for(T item : distinctValues)
			if(!contains(distinctRemovedValues, item))
				result.add(item);

		return result;
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T[] except(T[] values, T[] removedValues)
	{
		return except(values, removedValues, null);
	}

	/**
	 * Returns all distinct values except the specified removed values.
	 *
	 * @throws NullPointerException When the values or removedValues argument is null.
	 */
	public static <T> T[] except(T[] values, T[] removedValues, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(removedValues == null)
			throw new NullPointerException("removedValues");

		T[] distinctValues;
		T[] distinctRemovedValues;

		if(comparer == null)
		{
			distinctValues = distinct(values);
			distinctRemovedValues = distinct(removedValues);
		}
		else
		{
			distinctValues = distinct(values, comparer);
			distinctRemovedValues = distinct(removedValues, comparer);
		}

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T item : distinctValues)
			if(!contains(distinctRemovedValues, item))
				result.add(item);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Returns the first element in the provided sequence.
	 *
	 * @throws NullPointerException   When an argument is null.
	 * @throws NoSuchElementException There is no first element.
	 */
	public static <T> T first(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		for(T item : values)
			return item;

		throw new NoSuchElementException("The iterable is empty.");
	}

	/**
	 * Returns the first element in the provided sequence.
	 *
	 * @throws NullPointerException   When an argument is null.
	 * @throws NoSuchElementException There is no first element.
	 */
	public static <T> T first(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(values.length <= 0)
			throw new NoSuchElementException("The array is empty.");

		return values[0];
	}

	/**
	 * Returns the first element in the provided sequence that matches a condition.
	 *
	 * @throws NullPointerException   When an argument is null.
	 * @throws NoSuchElementException There is no match to the given predicate.
	 */
	public static <T> T first(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		for(T element : values)
			if(predicate.test(element))
				return element;

		throw new NoSuchElementException("There is no match to the given predicate.");
	}

	/**
	 * Returns the first element in the provided sequence that matches a condition.
	 *
	 * @throws NullPointerException   When an argument is null.
	 * @throws NoSuchElementException There is no match to the given predicate.
	 */
	public static <T> T first(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T element = values[i];
			if(predicate.test(element))
				return element;
		}

		throw new NoSuchElementException("There is no match to the given predicate.");
	}

	/**
	 * Returns the first element in the provided sequence.
	 * If no element is found, then null is returned.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T firstOrDefault(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		for(T item : values)
			return item;

		return null;
	}

	/**
	 * Returns the first element in the provided sequence that matches a condition.
	 * It will return the default value of T if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T firstOrDefault(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		for(T element : values)
			if(predicate.test(element))
				return element;

		return null;
	}

	/**
	 * Returns the first element in the provided array.
	 * If no element is found, then null is returned.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T firstOrDefault(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(values.length == 0)
			return null;

		return values[0];
	}

	/**
	 * Returns the first element in the provided sequence that matches a condition.
	 * It will return the default value of T if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T firstOrDefault(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T element = values[i];
			if(predicate.test(element))
				return element;
		}

		return null;
	}

	/**
	 * Groups elements by a specified key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey, TResult> ReifiedList<TResult> groupBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector)
	{
		return groupBy(values, keySelector, null);
	}

	/**
	 * Groups elements by a specified key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey, TResult> TResult[] groupBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector)
	{
		return groupBy(values, keySelector, null);
	}

	/**
	 * Groups elements by a specified key and comparer.
	 *
	 * @throws NullPointerException When the values argument or the key selector is null.
	 */
	public static <TKey, TResult> ReifiedList<TResult> groupBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");

		TreeMap<TKey, TResult> lookup;
		if(comparer == null)
			lookup = new TreeMap<TKey, TResult>();
		else
			lookup = new TreeMap<TKey, TResult>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);

			if(!lookup.containsKey(key))
				lookup.put(key, item);
		}

		return toList(lookup.values(), keySelector.getGenericTypeParameter());
	}

	/**
	 * Groups elements by a specified key and comparer.
	 *
	 * @throws NullPointerException When the values argument or the key selector is null.
	 */
	public static <TKey, TResult> TResult[] groupBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");

		TreeMap<TKey, TResult> lookup;
		if(comparer == null)
			lookup = new TreeMap<TKey, TResult>();
		else
			lookup = new TreeMap<TKey, TResult>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);

			if(!lookup.containsKey(key))
				lookup.put(key, item);
		}

		return toArray(lookup.values(), values.getClass().getComponentType());
	}

	/**
	 * Returns the index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int indexOf(Iterable<T> values, T element)
	{
		if(values == null)
			throw new NullPointerException("values");

		return element == null ? indexOfNull(values) : indexOfNotNull(values, element);
	}

	/**
	 * Returns the index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException When the values or the comparer argument is null.
	 */
	public static <T> int indexOf(Iterable<T> values, T element, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		return element == null ? indexOfNull(values) : indexOfNotNull(values, element, comparer);
	}

	/**
	 * Returns the index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int indexOf(T[] values, T element)
	{
		if(values == null)
			throw new NullPointerException("values");

		return element == null ? indexOfNull(values) : indexOfNotNull(values, element);
	}

	/**
	 * Returns the index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException When the values argument is null.
	 */
	public static <T> int indexOf(T[] values, T element, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		return element == null ? indexOfNull(values) : indexOfNotNull(values, element, comparer);
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> List<T> intersect(Iterable<T> first, Iterable<T> second)
	{
		return intersect(first, second, null);
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> ReifiedList<T> intersect(ReifiedIterable<T> first, ReifiedIterable<T> second)
	{
		return intersect(first, second, null);
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> List<T> intersect(Iterable<T> first, Iterable<T> second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		List<T> distinctFirst;
		List<T> distinctSecond;

		if(comparer == null)
		{
			distinctFirst = distinct(first);
			distinctSecond = distinct(second);
		}
		else
		{
			distinctFirst = distinct(first, comparer);
			distinctSecond = distinct(second, comparer);
		}

		for(T item : distinctFirst)
			if(contains(distinctSecond, item))
				result.add(item);

		return result;
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> ReifiedList<T> intersect(ReifiedIterable<T> first, ReifiedIterable<T> second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		ReifiedList<T> result = new ReifiedArrayList(DEFAULT_LIST_SIZE, first.getGenericTypeParameter());

		ReifiedList<T> distinctFirst;
		ReifiedList<T> distinctSecond;

		if(comparer == null)
		{
			distinctFirst = distinct(first);
			distinctSecond = distinct(second);
		}
		else
		{
			distinctFirst = distinct(first, comparer);
			distinctSecond = distinct(second, comparer);
		}

		for(T item : distinctFirst)
			if(contains(distinctSecond, item))
				result.add(item);

		return result;
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T[] intersect(T[] first, T[] second)
	{
		return intersect(first, second, null);
	}

	/**
	 * Returns the intersection of the distinct elements of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> T[] intersect(T[] first, T[] second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		T[] distinctFirst;
		T[] distinctSecond;

		if(comparer == null)
		{
			distinctFirst = distinct(first);
			distinctSecond = distinct(second);
		}
		else
		{
			distinctFirst = distinct(first, comparer);
			distinctSecond = distinct(second, comparer);
		}

		for(T item : distinctFirst)
			if(contains(distinctSecond, item))
				result.add(item);

		return toArray(result, first.getClass().getComponentType());
	}

	/**
	 * Returns true if the sequence is empty.
	 *
	 * @throws NullPointerException Is the argument is null.
	 */
	public static <T> boolean isEmpty(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		for(T val : values)
			return false;

		return true;
	}

	/**
	 * Returns true if the array is empty.
	 *
	 * @throws NullPointerException Is the argument is null.
	 */
	public static <T> boolean isEmpty(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		return values.length == 0;
	}

	/**
	 * Returns the last element in the iterable.
	 *
	 * @throws NullPointerException   If the array is null
	 * @throws NoSuchElementException If the iterable is empty
	 */
	public static <T> T last(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		// check if any items present
		T last;
		try
		{
			last = first(values);
		}
		catch(NoSuchElementException e)
		{
			// no last element
			throw new NoSuchElementException("The iterable is empty.");
		}

		// find last
		for(T item : values)
			last = item;

		return last;
	}

	/**
	 * Returns the last element in the array.
	 *
	 * @throws NullPointerException   If the array is null
	 * @throws NoSuchElementException If the array is empty
	 */
	public static <T> T last(T[] array)
	{
		if(array == null)
			throw new NullPointerException("array");
		if(array.length <= 0)
			throw new NoSuchElementException("The array is empty.");

		return array[array.length - 1];
	}

	/**
	 * Returns the last element in the provided sequence that matches a condition.
	 *
	 * @throws NullPointerException   The values argument is null.
	 * @throws NoSuchElementException There is no match to the given predicate
	 */
	public static <T> T last(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		T result = null;
		boolean found = false;

		for(T element : values)
			if(predicate.test(element))
			{
				found = true;
				result = element;
			}

		if(found)
			return result;

		throw new NoSuchElementException("There is no match to the given predicate.");
	}

	/**
	 * Returns the last element in the provided sequence that matches a condition.
	 *
	 * @throws NullPointerException   The values argument is null.
	 * @throws NoSuchElementException There is no match to the given predicate
	 */
	public static <T> T last(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		T result = null;
		boolean found = false;

		for(T element : values)
			if(predicate.test(element))
			{
				found = true;
				result = element;
			}

		if(found)
			return result;

		throw new NoSuchElementException("There is no match to the given predicate.");
	}

	/**
	 * Returns the last element in the provided sequence.
	 * It will return null if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T lastOrDefault(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		T result = null;
		boolean found = false;

		for(T item : values)
		{
			result = item;
			found = true;
		}

		return found ? result : (T) null;
	}

	/**
	 * Returns the last element in the provided array.
	 * It will return null if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T lastOrDefault(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		if(values.length <= 0)
			return null;

		return values[values.length - 1];
	}

	/**
	 * Returns the last element in the provided sequence that matches a condition.
	 * It will return null if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T lastOrDefault(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		T result = null;

		for(T element : values)
			if(predicate.test(element))
				result = element;

		return result;
	}

	/**
	 * Returns the last element in the provided sequence that matches a condition.
	 * It will return null if there is no match.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T lastOrDefault(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		T result = null;

		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T element = values[i];
			if(predicate.test(element))
				result = element;
		}

		return result;
	}

	/**
	 * Returns the last index where the specified element is found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException If the values argument is null.
	 */
	public static <T> int lastIndexOf(Iterable<? super T> values, T element)
	{
		if(values == null)
			throw new NullPointerException("values");

		return element == null ? lastIndexOfNull(values) : lastIndexOfNotNull(values, element);
	}

	/**
	 * Returns the last index where the specified element is found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException If the values or comparer argument is null.
	 */
	public static <T> int lastIndexOf(Iterable<T> values, T element, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		return element == null ? lastIndexOfNull(values) : lastIndexOfNotNull(values, element, comparer);
	}

	/**
	 * Returns the last index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException If the values argument is null.
	 */
	public static <T> int lastIndexOf(T[] values, T element)
	{
		if(values == null)
			throw new NullPointerException("values");

		return element == null ? lastIndexOfNull(values) : lastIndexOfNotNull(values, element);
	}

	/**
	 * Returns the last index where the specified element is first found. You may search for nulls.
	 * If the element is not found, this returns -1.
	 *
	 * @throws NullPointerException If the values or comparer argument is null.
	 */
	public static <T> int lastIndexOf(T[] values, T element, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparer == null)
			throw new NullPointerException("comparer");

		return element == null ? lastIndexOfNull(values) : lastIndexOfNotNull(values, element, comparer);
	}

	/**
	 * Performs an inner join (more specifically an equi-join) over two sequences.
	 * The outer values are filtered for key uniqueness (first encountered key kept), whereas inner values may be non-unique.
	 *
	 * @throws NullPointerException If an argument is null.
	 */
	public static <TOuter, TInner, TKey extends Comparable<TKey>, TResult> ReifiedList<TResult> join(Iterable<TOuter> outerValues, Iterable<TInner> innerValues, FunctionWithOneArgument<TOuter, TKey> outerKeySelector, FunctionWithOneArgument<TInner, TKey> innerKeySelector, FunctionWithTwoArguments<TOuter, TInner, TResult> resultSelector)
	{
		if(outerValues == null)
			throw new NullPointerException("outerValues");
		if(innerValues == null)
			throw new NullPointerException("innerValues");
		if(outerKeySelector == null)
			throw new NullPointerException("outerKeySelector");
		if(innerKeySelector == null)
			throw new NullPointerException("innerKeySelector");
		if(resultSelector == null)
			throw new NullPointerException("resultSelector");

		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, resultSelector.getGenericReturnTypeParameter());
		AvlHashtable<TKey, TOuter> lookup = new AvlHashtable<TKey, TOuter>(outerKeySelector.getGenericReturnTypeParameter(), outerKeySelector.getGenericTypeParameter());

		for(TOuter value : outerValues)
		{
			TKey outerKey = outerKeySelector.operateOn(value);
			lookup.add(outerKey, value);
		}

		for(TInner inner : innerValues)
		{
			TKey innerKey = innerKeySelector.operateOn(inner);

			if(lookup.containsKey(innerKey))
			{
				TOuter outer = lookup.get(innerKey);
				TResult res = resultSelector.operateOn(outer, inner);
				result.add(res);
			}
		}

		return result;
	}

	/**
	 * Performs an inner join (more specifically an equi-join) over two sequences.
	 * The outer values are filtered for key uniqueness (first encountered key kept), whereas inner values may be non-unique.
	 *
	 * @throws NullPointerException If an argument is null.
	 */
	public static <TOuter, TInner, TKey extends Comparable<TKey>, TResult> TResult[] join(TOuter[] outerValues, TInner[] innerValues, FunctionWithOneArgument<TOuter, TKey> outerKeySelector, FunctionWithOneArgument<TInner, TKey> innerKeySelector, FunctionWithTwoArguments<TOuter, TInner, TResult> resultSelector)
	{
		if(outerValues == null)
			throw new NullPointerException("outerValues");
		if(innerValues == null)
			throw new NullPointerException("innerValues");
		if(outerKeySelector == null)
			throw new NullPointerException("outerKeySelector");
		if(innerKeySelector == null)
			throw new NullPointerException("innerKeySelector");
		if(resultSelector == null)
			throw new NullPointerException("resultSelector");

		List<TResult> result = new ArrayList<TResult>(DEFAULT_LIST_SIZE);
		AvlHashtable<TKey, TOuter> lookup = new AvlHashtable<TKey, TOuter>(outerKeySelector.getGenericReturnTypeParameter(), outerKeySelector.getGenericTypeParameter());

		for(TOuter value : outerValues)
		{
			TKey outerKey = outerKeySelector.operateOn(value);
			lookup.add(outerKey, value);
		}

		for(TInner inner : innerValues)
		{
			TKey innerKey = innerKeySelector.operateOn(inner);

			if(lookup.containsKey(innerKey))
			{
				TOuter outer = lookup.get(innerKey);
				TResult res = resultSelector.operateOn(outer, inner);
				result.add(res);
			}
		}

		return toArray(result, resultSelector.getGenericReturnTypeParameter());
	}

	/**
	 * Returns all values in a sequence that are of a particular type.
	 * This operates differently to Cast, in that it does not force a cast; it rather checks if a TSource is of TDest type.
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <TSource, TDest> ReifiedList<TDest> ofType(Iterable<TSource> values, Class<TDest> destinationClass)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(destinationClass == null)
			throw new NullPointerException("destinationClass");

		ReifiedList<TDest> result = new ReifiedArrayList<TDest>(DEFAULT_LIST_SIZE, destinationClass);

		for(TSource item : values)
		{
			try
			{
				TDest temp = destinationClass.cast(item);
				result.add(temp);
			}
			catch(ClassCastException e)
			{
				continue;
			}
		}

		return result;
	}

	/**
	 * Returns all values in a sequence that are of a particular type.
	 * This operates differently to Cast, in that it does not force a cast; it rather checks if a TSource is of TDest type.
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <TSource, TDest> TDest[] ofType(TSource[] values, Class<TDest> destinationClass)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(destinationClass == null)
			throw new NullPointerException("destinationClass");

		ReifiedList<TDest> result = new ReifiedArrayList<TDest>(DEFAULT_LIST_SIZE, destinationClass);

		for(TSource item : values)
		{
			try
			{
				TDest temp = destinationClass.cast(item);
				result.add(temp);
			}
			catch(ClassCastException e)
			{
				continue;
			}
		}

		return result.toArray();
	}

	/**
	 * Orders a sequence by a specified key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey extends Comparable<TKey>, TResult> ReifiedList<TResult> orderBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector)
	{
		return orderBy(values, keySelector, null);
	}

	/**
	 * Orders a sequence by a specified key.
	 *
	 * @throws NullPointerException When the values or keySelector argument is null.
	 */
	public static <TKey extends Comparable<TKey>, TResult> ReifiedList<TResult> orderBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");

		TreeMap<TKey, List<TResult>> dict;
		if(comparer == null)
			dict = new TreeMap<TKey, List<TResult>>();
		else
			dict = new TreeMap<TKey, List<TResult>>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);
			if(dict.containsKey(key))
				dict.get(key).add(item);
			else
			{
				List<TResult> val = new ArrayList<TResult>();
				val.add(item);
				dict.put(key, val);
			}
		}

		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, keySelector.getGenericTypeParameter());
		for(List<TResult> list : dict.values())
			result.addAll(list);

		return result;
	}

	/**
	 * Orders a sequence by a specified key.
	 *
	 * @throws NullPointerException When the values or keySelector argument is null.
	 */
	public static <TKey, TResult> TResult[] orderBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");

		TreeMap<TKey, List<TResult>> dict;
		if(comparer == null)
			dict = new TreeMap<TKey, List<TResult>>();
		else
			dict = new TreeMap<TKey, List<TResult>>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);
			if(dict.containsKey(key))
				dict.get(key).add(item);
			else
			{
				List<TResult> val = new ArrayList<TResult>();
				val.add(item);
				dict.put(key, val);
			}
		}

		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, keySelector.getGenericTypeParameter());
		for(List<TResult> list : dict.values())
			result.addAll(list);

		return result.toArray();
	}

	/**
	 * Orders a sequence by a specified key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey extends Comparable<TKey>, TResult> TResult[] orderBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector)
	{
		return orderBy(values, keySelector, null);
	}

	/**
	 * Orders a sequence by a specified key and matching key results get sorted by a second key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey extends Comparable<TKey>, TKey2 extends Comparable<TKey>, TResult> ReifiedList<TResult> orderByThenBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector, FunctionWithOneArgument<TResult, TKey2> keySelector2)
	{
		return orderByThenBy(values, keySelector, null, keySelector2, null);
	}

	/**
	 * Orders a sequence by a specified key and matching key results get sorted by a second key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey extends Comparable<TKey>, TKey2 extends Comparable<TKey>, TResult> TResult[] orderByThenBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector, FunctionWithOneArgument<TResult, TKey2> keySelector2)
	{
		return orderByThenBy(values, keySelector, null, keySelector2, null);
	}

	/**
	 * Orders a sequence by a specified key and matching key results get sorted by a second key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey, TKey2, TResult> ReifiedList<TResult> orderByThenBy(Iterable<TResult> values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer, FunctionWithOneArgument<TResult, TKey2> keySelector2, Comparator<? super TKey2> comparer2)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");
		if(keySelector2 == null)
			throw new NullPointerException("keySelector2");

		TreeMap<TKey, TreeMap<TKey2, List<TResult>>> dict;
		if(comparer == null)
			dict = new TreeMap<TKey, TreeMap<TKey2, List<TResult>>>();
		else
			dict = new TreeMap<TKey, TreeMap<TKey2, List<TResult>>>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);
			TKey2 key2 = keySelector2.operateOn(item);

			if(dict.containsKey(key))
			{
				if(dict.get(key).containsKey(key2))
					dict.get(key).get(key2).add(item);
				else
				{
					List<TResult> lst = new ArrayList<TResult>();
					lst.add(item);
					dict.get(key).put(key2, lst);
				}
			}
			else
			{
				TreeMap<TKey2, List<TResult>> secondDictionary;

				if(comparer2 == null)
					secondDictionary = new TreeMap<TKey2, List<TResult>>();
				else
					secondDictionary = new TreeMap<TKey2, List<TResult>>(comparer2);

				List<TResult> lst = new ArrayList<TResult>();
				lst.add(item);
				secondDictionary.put(key2, lst);
				dict.put(key, secondDictionary);
			}
		}

		// get all lists and combine into one resultant list
		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, keySelector2.getGenericReturnTypeParameter());
		// get all secondary dictionaries
		for(TreeMap<TKey2, List<TResult>> tree : dict.values())
			for(List<TResult> list : tree.values())
				result.addAll(list);

		return result;
	}

	/**
	 * Orders a sequence by a specified key and matching key results get sorted by a second key.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TKey, TKey2, TResult> TResult[] orderByThenBy(TResult[] values, FunctionWithOneArgument<TResult, TKey> keySelector, Comparator<? super TKey> comparer, FunctionWithOneArgument<TResult, TKey2> keySelector2, Comparator<? super TKey2> comparer2)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");
		if(keySelector2 == null)
			throw new NullPointerException("keySelector2");

		TreeMap<TKey, TreeMap<TKey2, List<TResult>>> dict;
		if(comparer == null)
			dict = new TreeMap<TKey, TreeMap<TKey2, List<TResult>>>();
		else
			dict = new TreeMap<TKey, TreeMap<TKey2, List<TResult>>>(comparer);

		for(TResult item : values)
		{
			TKey key = keySelector.operateOn(item);
			TKey2 key2 = keySelector2.operateOn(item);

			if(dict.containsKey(key))
			{
				if(dict.get(key).containsKey(key2))
					dict.get(key).get(key2).add(item);
				else
				{
					List<TResult> lst = new ArrayList<TResult>();
					lst.add(item);
					dict.get(key).put(key2, lst);
				}
			}
			else
			{
				TreeMap<TKey2, List<TResult>> secondDictionary;

				if(comparer2 == null)
					secondDictionary = new TreeMap<TKey2, List<TResult>>();
				else
					secondDictionary = new TreeMap<TKey2, List<TResult>>(comparer2);

				List<TResult> lst = new ArrayList<TResult>();
				lst.add(item);
				secondDictionary.put(key2, lst);
				dict.put(key, secondDictionary);
			}
		}

		// get all lists and combine into one resultant list
		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, keySelector2.getGenericReturnTypeParameter());
		// get all secondary dictionaries
		for(TreeMap<TKey2, List<TResult>> tree : dict.values())
			for(List<TResult> list : tree.values())
				result.addAll(list);

		return result.toArray();
	}

	/**
	 * Returns a range from the provided sequence. Inclusiveness is [start, finish) i.e. as in a For loop.
	 *
	 * @throws NullPointerException	  The values argument is null.
	 * @throws IndexOutOfBoundsException An index is out of range.
	 */
	public static <T> List<T> range(Iterable<T> values, int start, int finish)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(start < 0)
			throw new IndexOutOfBoundsException("start=" + start);
		if(finish < start)
			throw new IndexOutOfBoundsException("start=" + start + " finish=" + finish);

		List<T> result = new ArrayList<T>(finish - start);

		int index = 0;
		for(T item : values)
		{
			if(index >= finish)
				break;
			if(index >= start)
				result.add(item);

			index++;
		}

		if(index < finish)
			throw new IndexOutOfBoundsException("max=" + index + " finish=" + finish);

		return result;
	}

	/**
	 * Returns a range from the provided sequence. Inclusiveness is [start, finish) i.e. as in a For loop.
	 *
	 * @throws NullPointerException	  The values argument is null.
	 * @throws IndexOutOfBoundsException An index is out of range.
	 */
	public static <T> ReifiedList<T> range(ReifiedIterable<T> values, int start, int finish)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(start < 0)
			throw new IndexOutOfBoundsException("start=" + start);
		if(finish < start)
			throw new IndexOutOfBoundsException("start=" + start + " finish=" + finish);

		ReifiedList<T> result = new ReifiedArrayList<T>(finish - start, values.getGenericTypeParameter());

		int index = 0;
		for(T item : values)
		{
			if(index >= finish)
				break;
			if(index >= start)
				result.add(item);

			index++;
		}

		if(index < finish)
			throw new IndexOutOfBoundsException("max=" + index + " finish=" + finish);

		return result;
	}

	/**
	 * Returns a range from the provided sequence. Inclusiveness is [start, finish) i.e. as in a For loop.
	 *
	 * @throws NullPointerException	  The values argument is null.
	 * @throws IndexOutOfBoundsException An index is out of range.
	 */
	public static <T> T[] range(T[] values, int start, int finish)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(start < 0)
			throw new IndexOutOfBoundsException("start=" + start);
		if(finish < start || finish > values.length)
			throw new IndexOutOfBoundsException("start=" + start + " finish=" + finish + " length=" + values.length);

		List<T> result = new ArrayList<T>(finish - start);

		for(int i = start; i < finish; i++)
			result.add(values[i]);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Returns a range of values, from start to end (exclusive).
	 * The value is incremented using the specified stepping function.
	 *
	 * @throws NullPointerException The step function argument is null.
	 */
	public static <T> ReifiedList<T> range(T start, T end, FunctionWithOneArgument<T, T> stepFunction)
	{
		if(stepFunction == null)
			throw new NullPointerException("stepFunction");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, stepFunction.getGenericReturnTypeParameter());

		T current = start;

		if(start == null || end == null)
			while(current != end)
			{
				result.add(current);
				current = stepFunction.operateOn(current);
			}
		else
			while(!current.equals(end))
			{
				result.add(current);
				current = stepFunction.operateOn(current);
			}

		return result;
	}

	/**
	 * Returns a range of values, by using a step function, until the predicate returns false
	 *
	 * @throws NullPointerException The predicate or step function argument is null.
	 */
	public static <T> ReifiedList<T> range(T start, Predicate<? super T> predicate, FunctionWithOneArgument<T, T> stepFunction)
	{
		if(predicate == null)
			throw new NullPointerException("predicate");
		if(stepFunction == null)
			throw new NullPointerException("stepFunction");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, stepFunction.getGenericReturnTypeParameter());

		T current = start;
		while(predicate.test(current))
		{
			result.add(current);
			current = stepFunction.operateOn(current);
		}

		return result;
	}

	/**
	 * Returns a collection of specified size
	 *
	 * @throws IllegalArgumentException The count is out of range.
	 */
	public static <T> List<T> repeat(T value, int count)
	{
		if(count < 0)
			throw new IllegalArgumentException("count=" + count);

		List<T> result = new ArrayList<T>(count);

		for(int i = 0; i < count; i++)
			result.add(value);

		return result;
	}

	/**
	 * Returns a collection of specified size
	 *
	 * @throws IllegalArgumentException The count is out of range.
	 * @throws NullPointerException	 When the destination class is null.
	 */
	public static <T> ReifiedList<T> repeat(T value, Class<?> destinationClass, int count)
	{
		if(count < 0)
			throw new IllegalArgumentException("count=" + count);

		ReifiedList<T> result = new ReifiedArrayList<T>(count, destinationClass);

		for(int i = 0; i < count; i++)
			result.add(value);

		return result;
	}

	/**
	 * Returns a reversed version of the provided sequence
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <T> List<T> reverse(List<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		Collections.reverse(values);

		return values;
	}

	/**
	 * Returns a reversed version of the provided sequence
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <T> List<T> reverse(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			result.add(item);

		Collections.reverse(result);

		return result;
	}

	/**
	 * Returns a reversed version of the provided sequence
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <T> ReifiedList<T> reverse(ReifiedIterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedList<T> result = new ReifiedArrayList<T>(values);
		Collections.reverse(result);

		return result;
	}

	/**
	 * Returns a reversed version of the provided array
	 *
	 * @throws NullPointerException When the argument is null.
	 */
	public static <T> T[] reverse(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = Arrays.asList(values);
		Collections.reverse(result);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Acts as a Select LINQ function.
	 * It will never return null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TSource, TResult> ReifiedList<TResult> select(Iterable<TSource> values, FunctionWithOneArgument<TSource, TResult> selector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(selector == null)
			throw new NullPointerException("selector");

		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, selector.getGenericReturnTypeParameter());

		for(TSource item : values)
			result.add(selector.operateOn(item));

		return result;
	}

	/**
	 * Acts as a Select LINQ function.
	 * It will never return null.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TSource, TResult> TResult[] select(TSource[] values, FunctionWithOneArgument<TSource, TResult> selector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(selector == null)
			throw new NullPointerException("selector");

		List<TResult> result = new ArrayList<TResult>(values.length);

		for(TSource item : values)
			result.add(selector.operateOn(item));

		return toArray(result, selector.getGenericReturnTypeParameter());
	}

	/**
	 * Acts as a SelectMany LINQ function, to allow selection of iterables and return all their sub-items.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TSource, TResult> ReifiedList<TResult> selectMany(Iterable<TSource> values, FunctionWithOneArgument<TSource, ReifiedList<TResult>> selector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(selector == null)
			throw new NullPointerException("selector");

		List<TResult> result = new ArrayList<TResult>(DEFAULT_LIST_SIZE);
		Class<?> resultClass = null;

		for(TSource item : values)
		{
			ReifiedList<TResult> subItems = selector.operateOn(item);
			if(subItems != null)
			{
				for(TResult subItem : subItems)
					result.add(subItem);
				if(resultClass == null)
					resultClass = subItems.getGenericTypeParameter();
			}
		}

		if(resultClass == null)
			throw new IllegalArgumentException("Could not determine run-time type because all selection results were null.");

		return toList(result, resultClass);
	}

	/**
	 * Acts as a SelectMany LINQ function, to allow selection of iterables and return all their sub-items.
	 *
	 * @throws NullPointerException	 When an argument is null.
	 * @throws IllegalArgumentException When the run-time type of the resulting array cannot be determined.
	 */
	public static <TSource, TResult> TResult[] selectMany(TSource[] values, FunctionWithOneArgument<TSource, ReifiedList<TResult>> selector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(selector == null)
			throw new NullPointerException("selector");

		List<TResult> result = new ArrayList<TResult>(DEFAULT_LIST_SIZE);
		Class<?> resultClass = null;

		for(TSource item : values)
		{
			ReifiedList<TResult> subItems = selector.operateOn(item);
			if(subItems != null)
			{
				for(TResult subItem : subItems)
					result.add(subItem);
				if(resultClass == null)
					resultClass = subItems.getGenericTypeParameter();
			}
		}

		if(resultClass == null)
			throw new IllegalArgumentException("Could not determine run-time type because all selection results were null.");

		return toArray(result, resultClass);
	}

	/**
	 * Returns true if both iterables have the same values in the exact same positions.
	 *
	 * @throws NullPointerException An argument is null
	 */
	public static <T> boolean sequenceEqual(Iterable<? super T> values1, Iterable<T> values2)
	{
		if(values1 == null)
			throw new NullPointerException("values1");
		if(values2 == null)
			throw new NullPointerException("values2");

		if(count(values1) != count(values2))
			return false;
		else
		{
			Iterator<? super T> i1 = values1.iterator();
			Iterator<T> i2 = values2.iterator();

			// enumerate both
			while(i1.hasNext())
			{
				Object v1 = i1.next();
				T v2 = i2.next();

				// compare using Equals if both not null
				if(v1 != null && v2 != null)
				{
					if(!v1.equals(v2))
						return false;
				}
				else
					// check if one is null and the other is not
					if(v1 != null || v2 != null)
						return false;
			}

			return true;
		}
	}

	/**
	 * Returns true if both iterables have the same values in the exact same positions.
	 *
	 * @throws NullPointerException An argument is null
	 */
	public static <T> boolean sequenceEqual(T[] values1, T[] values2)
	{
		if(values1 == null)
			throw new NullPointerException("values1");
		if(values2 == null)
			throw new NullPointerException("values2");

		if(values1.length != values2.length)
			return false;
		else
		{
			int count = values1.length;
			for(int i = 0; i < count; i++)
			{
				T v1 = values1[i];
				T v2 = values2[i];

				// compare using Equals if both not null
				if(v1 != null && v2 != null)
				{
					if(!v1.equals(v2))
						return false;
				}
				else
					// check if one is null and the other is not
					if(v1 != null || v2 != null)
						return false;
			}

			return true;
		}
	}

	/**
	 * Skips up to the specified number of elements in the given sequence.
	 *
	 * @throws NullPointerException	 When the values argument is null
	 * @throws IllegalArgumentException When count is out of range.
	 */
	public static <T> List<T> skip(Iterable<T> values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(count < 0)
			throw new IllegalArgumentException("count=" + count);

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		int i = 0;
		for(T item : values)
		{
			if(i++ < count)
				continue;

			result.add(item);
		}

		return result;
	}

	/**
	 * Skips up to the specified number of elements in the given sequence.
	 *
	 * @throws NullPointerException	 When the values argument is null
	 * @throws IllegalArgumentException When count is out of range.
	 */
	public static <T> ReifiedList<T> skip(ReifiedIterable<T> values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(count < 0)
			throw new IllegalArgumentException("count=" + count);

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());

		int i = 0;
		for(T item : values)
		{
			if(i++ < count)
				continue;

			result.add(item);
		}

		return result;
	}

	/**
	 * Skips up to the specified number of elements in the given sequence.
	 *
	 * @throws NullPointerException	 When the values argument is null
	 * @throws IllegalArgumentException When count is out of range.
	 */
	public static <T> T[] skip(T[] values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(count < 0)
			throw new IllegalArgumentException("count=" + count);

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		int i = 0;
		for(T item : values)
		{
			if(i++ < count)
				continue;

			result.add(item);
		}

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Skips items in the sequence for which a predicate is true, returning the rest.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> List<T> skipWhile(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		boolean skipping = true;

		for(T item : values)
		{
			if(skipping)
			{
				if(!predicate.test(item))
				{
					skipping = false;
					result.add(item);
				}
			}
			else
				result.add(item);
		}

		return result;
	}

	/**
	 * Skips items in the sequence for which a predicate is true, returning the rest.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> ReifiedList<T> skipWhile(ReifiedIterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE);
		boolean skipping = true;

		for(T item : values)
		{
			if(skipping)
			{
				if(!predicate.test(item))
				{
					skipping = false;
					result.add(item);
				}
			}
			else
				result.add(item);
		}

		return result;
	}

	/**
	 * Skips items in the sequence for which a predicate is true, returning the rest.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> T[] skipWhile(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		boolean skipping = true;

		for(T item : values)
		{
			if(skipping)
			{
				if(!predicate.test(item))
				{
					skipping = false;
					result.add(item);
				}
			}
			else
				result.add(item);
		}

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Sorts a list
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T extends Comparable<T>> List<T> sort(List<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		Collections.sort(values);
		return values;
	}

	/**
	 * Sorts a sequence.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T extends Comparable<T>> List<T> sort(Iterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T item : values)
			result.add(item);

		Collections.sort(result);
		return result;
	}

	/**
	 * Sorts a sequence.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T extends Comparable<T>> ReifiedList<T> sort(ReifiedIterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedList<T> result = new ReifiedArrayList<T>(values);
		Collections.sort(result);
		return result;
	}

	/**
	 * Sorts a sequence.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> List<T> sort(Iterable<T> values, Comparator<? super T> comparison)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparison == null)
			throw new NullPointerException("comparison");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			result.add(item);

		Collections.sort(result, comparison);

		return result;
	}

	/**
	 * Sorts a sequence.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> ReifiedList<T> sort(ReifiedIterable<T> values, Comparator<? super T> comparison)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(comparison == null)
			throw new NullPointerException("comparison");

		ReifiedList<T> result = new ReifiedArrayList<T>(values);
		Collections.sort(result, comparison);

		return result;
	}

	/**
	 * Splits a sequence into parts delimited by the specified delimited.
	 * Empty entries between delimiters are removed.
	 *
	 * @throws NullPointerException When an argument is null, or an item in the iterable is null.
	 */
	public static <T> List<T>[] split(Iterable<? extends T> values, T delimiter)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(delimiter == null)
			throw new NullPointerException("delimiter");

		ReifiedList<List<T>> parts = new ReifiedArrayList<List<T>>()
		{
		};
		parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));

		for(T item : values)
		{
			if(!item.equals(delimiter))
				parts.get(parts.size() - 1).add(item);
			else
				parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));
		}

		return where(parts, new Predicate<List<T>>()
		{
			@Override
			public boolean test(List<T> element)
			{
				return element.size() > 0;
			}
		}).toArray();
	}

	/**
	 * Splits a sequence into parts delimited by the specified delimited.
	 * Empty entries between delimiters are removed.
	 *
	 * @throws NullPointerException When an argument is null, or an item in the iterable is null.
	 */
	public static <T> List<T>[] split(T[] values, T delimiter)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(delimiter == null)
			throw new NullPointerException("delimiter");

		ReifiedList<List<T>> parts = new ReifiedArrayList<List<T>>()
		{
		};
		parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));

		for(T item : values)
		{
			if(!item.equals(delimiter))
				parts.get(parts.size() - 1).add(item);
			else
				parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));
		}

		return where(parts, new Predicate<List<T>>()
		{
			@Override
			public boolean test(List<T> element)
			{
				return element.size() > 0;
			}
		}).toArray();
	}

	/**
	 * Splits a sequence into parts delimited by the specified delimited.
	 * Empty entries between delimiters are removed.
	 *
	 * @throws NullPointerException When an argument is null, or an item in the iterable is null and the comparer does not handle this case.
	 */
	public static <T> List<T>[] split(Iterable<T> values, T delimiter, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(delimiter == null)
			throw new NullPointerException("delimiter");

		ReifiedList<List<T>> parts = new ReifiedArrayList<List<T>>()
		{
		};
		parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));

		for(T item : values)
		{

			if(comparer.compare(item, delimiter) != 0)
				// not a delimiter, add to parts
				parts.get(parts.size() - 1).add(item);
			else
				parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));
		}

		return where(parts, new Predicate<List<T>>()
		{
			@Override
			public boolean test(List<T> element)
			{
				return element.size() > 0;
			}
		}).toArray();
	}

	/**
	 * Splits a sequence into parts delimited by the specified delimited.
	 * Empty entries between delimiters are removed.
	 *
	 * @throws NullPointerException When an argument is null, or an item in the iterable is null and the comparer does not handle this case.
	 */
	public static <T> List<T>[] split(T[] values, T delimiter, Comparator<? super T> comparer)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(delimiter == null)
			throw new NullPointerException("delimiter");

		ReifiedList<List<T>> parts = new ReifiedArrayList<List<T>>()
		{
		};
		parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));

		for(T item : values)
		{
			if(comparer.compare(item, delimiter) != 0)
				parts.get(parts.size() - 1).add(item);
			else
				parts.add(new ArrayList<T>(DEFAULT_LIST_SIZE));
		}

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		return where(parts, new Predicate<List<T>>()
		{
			@Override
			public boolean test(List<T> element)
			{
				return element.size() > 0;
			}
		}).toArray();
	}

	/**
	 * Returns up to the specified number of elements from the given sequence.
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> List<T> take(Iterable<T> values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		Iterator<T> iterator = values.iterator();

		int index = 0;
		while(index++ < count && iterator.hasNext())
			result.add(iterator.next());

		return result;
	}

	/**
	 * Returns up to the specified number of elements from the given sequence.
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> ReifiedList<T> take(ReifiedIterable<T> values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());

		Iterator<T> iterator = values.iterator();

		int index = 0;
		while(index++ < count && iterator.hasNext())
			result.add(iterator.next());

		return result;
	}

	/**
	 * Returns up to the specified number of elements from the given sequence.
	 *
	 * @throws NullPointerException	 The values argument is null.
	 * @throws IllegalArgumentException The count argument is out of range.
	 */
	public static <T> T[] take(T[] values, int count)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(count);
		for(int i = 0; i < count && i < values.length; i++)
			result.add(values[i]);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Returns items in the sequence while a predicate is true. Breaks when the condition is not satisfied.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> List<T> takeWhile(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T item : values)
			if(predicate.test(item))
				result.add(item);
			else
				break;

		return result;
	}

	/**
	 * Returns items in the sequence while a predicate is true. Breaks when the condition is not satisfied.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> ReifiedList<T> takeWhile(ReifiedIterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());

		for(T item : values)
			if(predicate.test(item))
				result.add(item);
			else
				break;

		return result;
	}

	/**
	 * Returns items in the sequence while a predicate is true. Breaks when the condition is not satisfied.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> T[] takeWhile(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T item = values[i];
			if(predicate.test(item))
				result.add(item);
			else
				break;
		}

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Converts a list to an array.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> T[] toArray(ReifiedList<T> list)
	{
		return toArray(list, list.getGenericTypeParameter());
	}

	/**
	 * Converts an iterable to an array.
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> T[] toArray(ReifiedIterable<T> values)
	{
		return toArray(values, values.getGenericTypeParameter());
	}

	/**
	 * Converts an iterable to an array.
	 *
	 * @throws NullPointerException An argument is nul.
	 */
	public static <T> T[] toArray(Iterable<T> values, Class<?> componentType)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(componentType == null)
			throw new NullPointerException("componentType");

		// count items
		int count = count(values);

		T[] result = (T[]) Array.newInstance(componentType, count);

		Iterator<T> iterator = values.iterator();

		for(int i = 0; i < count; i++)
			result[i] = iterator.next();

		return result;
	}

	/**
	 * Converts a collection to an array.
	 *
	 * @throws NullPointerException An argument is null.
	 */
	public static <T> T[] toArray(Collection<T> list, Class<?> componentType)
	{
		if(list == null)
			throw new NullPointerException("values");
		if(componentType == null)
			throw new NullPointerException("componentType");

		// count items
		int size = list.size();
		T[] result = (T[]) Array.newInstance(componentType, size);

		Iterator<T> iterator = list.iterator();

		for(int i = 0; i < size; i++)
			result[i] = iterator.next();

		return result;
	}

	/**
	 * Converts a sequence of items into a key/value AVL tree.
	 * Items from which duplicate keys are derived will be skipped.
	 * Null keys are not allowed.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T, TKey extends Comparable<TKey>, TValue> AvlHashtable<TKey, TValue> toAvlHashtable(Iterable<T> values, Class<?> genericTypeParameterKey, Class<?> genericTypeParameterValue, FunctionWithOneArgument<T, TKey> keySelector, FunctionWithOneArgument<T, TValue> valueSelector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");
		if(valueSelector == null)
			throw new NullPointerException("valueSelector");
		if(genericTypeParameterKey == null)
			throw new NullPointerException("genericTypeParameterKey");
		if(genericTypeParameterValue == null)
			throw new NullPointerException("genericTypeParameterValue");

		AvlHashtable<TKey, TValue> result = new AvlHashtable<TKey, TValue>(genericTypeParameterKey, genericTypeParameterValue);
		for(T item : values)
			result.add(keySelector.operateOn(item), valueSelector.operateOn(item));

		return result;
	}

	/**
	 * Converts a sequence of items into a key/value AVL hashtable.
	 * Items from which duplicate keys are derived will be skipped.
	 * Null keys are not allowed by the AVLHashtable.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T, TKey extends Comparable<TKey>, TValue> AvlHashtable<TKey, TValue> toAvlHashtable(T[] values, Class<?> genericTypeParameterKey, Class<?> genericTypeParameterValue, FunctionWithOneArgument<T, TKey> keySelector, FunctionWithOneArgument<T, TValue> valueSelector)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(keySelector == null)
			throw new NullPointerException("keySelector");
		if(valueSelector == null)
			throw new NullPointerException("valueSelector");
		if(genericTypeParameterKey == null)
			throw new NullPointerException("genericTypeParameterKey");
		if(genericTypeParameterValue == null)
			throw new NullPointerException("genericTypeParameterValue");

		AvlHashtable<TKey, TValue> result = new AvlHashtable<TKey, TValue>(genericTypeParameterKey, genericTypeParameterValue);
		int count = values.length;
		for(int i = 0; i < count; i++)
			result.add(keySelector.operateOn(values[i]), valueSelector.operateOn(values[i]));

		return result;
	}

	/**
	 * Converts an IEnumerable to an array
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> List<T> toList(Iterable<? extends T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);
		for(T item : values)
			result.add(item);

		return result;
	}

	/**
	 * Converts an iterable to a list
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> ReifiedList<T> toList(Iterable<T> values, Class<?> genericTypeParameter)
	{
		if(values == null)
			throw new NullPointerException("values");

		return new ReifiedArrayList<T>(values, genericTypeParameter);
	}

	/**
	 * Converts an iterable to a list
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> ReifiedList<T> toList(ReifiedIterable<T> values)
	{
		if(values == null)
			throw new NullPointerException("values");

		return new ReifiedArrayList<T>(values);
	}

	/**
	 * Converts an array to a list
	 *
	 * @throws NullPointerException The values argument is null.
	 */
	public static <T> ReifiedList<T> toList(T[] values)
	{
		if(values == null)
			throw new NullPointerException("values");

		return new ReifiedArrayList<T>(values);
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> List<T> union(Iterable<T> first, Iterable<T> second)
	{
		return union(first, second, null);
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> ReifiedList<T> union(ReifiedIterable<T> first, ReifiedIterable<T> second)
	{
		return union(first, second, null);
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> List<T> union(Iterable<T> first, Iterable<T> second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		List<T> firstDistinct;
		List<T> secondDistinct;

		if(comparer == null)
		{
			firstDistinct = distinct(first);
			secondDistinct = distinct(second);
			List<T> union = concat(firstDistinct, secondDistinct);
			return distinct(union);
		}
		else
		{
			firstDistinct = distinct(first, comparer);
			secondDistinct = distinct(second, comparer);
			List<T> union = concat(firstDistinct, secondDistinct);
			return distinct(union, comparer);
		}
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> ReifiedList<T> union(ReifiedIterable<T> first, ReifiedIterable<T> second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		ReifiedList<T> firstDistinct;
		ReifiedList<T> secondDistinct;

		if(comparer == null)
		{
			firstDistinct = distinct(first);
			secondDistinct = distinct(second);
			ReifiedList<T> union = concat(firstDistinct, secondDistinct);
			return distinct(union);
		}
		else
		{
			firstDistinct = distinct(first, comparer);
			secondDistinct = distinct(second, comparer);
			ReifiedList<T> union = concat(firstDistinct, secondDistinct);
			return distinct(union, comparer);
		}
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <T> T[] union(T[] first, T[] second)
	{
		return union(first, second, null);
	}

	/**
	 * Produces the union of two sequences.
	 *
	 * @throws NullPointerException When the first or second argument is null.
	 */
	public static <T> T[] union(T[] first, T[] second, Comparator<? super T> comparer)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");

		T[] firstDistinct;
		T[] secondDistinct;

		if(comparer == null)
		{
			firstDistinct = distinct(first);
			secondDistinct = distinct(second);
			T[] union = concat(firstDistinct, secondDistinct);
			return distinct(union);
		}
		else
		{
			firstDistinct = distinct(first, comparer);
			secondDistinct = distinct(second, comparer);
			T[] union = concat(firstDistinct, secondDistinct);
			return distinct(union, comparer);
		}
	}

	/**
	 * Returns a subset of the provided sequence, which conforms to the
	 * given predicate i.e. acts like a Where LINQ function
	 * It will never return null.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> List<T> where(Iterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T element : values)
			if(predicate.test(element))
				result.add(element);

		return result;
	}

	/**
	 * Returns a subset of the provided sequence, which conforms to the
	 * given predicate i.e. acts like a Where LINQ function
	 * It will never return null.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> ReifiedList<T> where(ReifiedIterable<T> values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		ReifiedList<T> result = new ReifiedArrayList<T>(DEFAULT_LIST_SIZE, values.getGenericTypeParameter());

		for(T element : values)
			if(predicate.test(element))
				result.add(element);

		return result;
	}

	/**
	 * Returns a subset of the provided sequence, which conforms to the
	 * given predicate i.e. acts like a Where LINQ function
	 * It will never return null.
	 *
	 * @throws NullPointerException When an argument is null
	 */
	public static <T> T[] where(T[] values, Predicate<? super T> predicate)
	{
		if(values == null)
			throw new NullPointerException("values");
		if(predicate == null)
			throw new NullPointerException("predicate");

		List<T> result = new ArrayList<T>(DEFAULT_LIST_SIZE);

		for(T element : values)
			if(predicate.test(element))
				result.add(element);

		return toArray(result, values.getClass().getComponentType());
	}

	/**
	 * Merges two sequences by using the specified predicate function.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TFirst, TSecond, TResult> ReifiedList<TResult> zip(Iterable<TFirst> first, Iterable<TSecond> second, FunctionWithTwoArguments<TFirst, TSecond, TResult> function)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");
		if(function == null)
			throw new NullPointerException("function");

		ReifiedList<TResult> result = new ReifiedArrayList<TResult>(DEFAULT_LIST_SIZE, function.getGenericReturnTypeParameter());

		Iterator<TFirst> iterator1 = first.iterator();
		Iterator<TSecond> iterator2 = second.iterator();
		while(iterator1.hasNext() && iterator2.hasNext())
			result.add(function.operateOn(iterator1.next(), iterator2.next()));

		return result;
	}

	/**
	 * Merges two sequences by using the specified predicate function.
	 *
	 * @throws NullPointerException When an argument is null.
	 */
	public static <TFirst, TSecond, TResult> TResult[] zip(TFirst[] first, TSecond[] second, FunctionWithTwoArguments<TFirst, TSecond, TResult> function)
	{
		if(first == null)
			throw new NullPointerException("first");
		if(second == null)
			throw new NullPointerException("second");
		if(function == null)
			throw new NullPointerException("function");

		List<TResult> result = new ArrayList<TResult>(DEFAULT_LIST_SIZE);

		int count = Math.min(first.length, second.length);

		for(int i = 0; i < count; i++)
			result.add(function.operateOn(first[i], second[i]));

		return toArray(result, function.getGenericReturnTypeParameter());
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * throwing an InvalidCastException if any elements are not cast successfully.
	 */
	private static <TSource, TDest> ReifiedList<TDest> castThrow(Iterable<TSource> values, Class<TDest> destinationClass)
	{
		ReifiedList<TDest> result = new ReifiedArrayList<TDest>(DEFAULT_LIST_SIZE, destinationClass);

		for(TSource val : values)
		{
			TDest castVal = (TDest) destinationClass.cast(val);
			result.add(castVal);
		}

		return result;
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * throwing an InvalidCastException if any elements are not cast successfully.
	 */
	private static <TSource, TDest> void castThrow(TSource[] values, ReifiedList<TDest> list)
	{
		Class<?> destinationClass = list.getGenericTypeParameter();
		for(TSource val : values)
		{
			TDest castVal = (TDest) destinationClass.cast(val);
			list.add(castVal);
		}
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * discarding elements that are not cast successfully.
	 */
	private static <TSource, TDest> ReifiedList<TDest> castRemove(Iterable<TSource> values, Class<TDest> destinationClass)
	{
		ReifiedList<TDest> result = new ReifiedArrayList<TDest>(DEFAULT_LIST_SIZE, destinationClass);

		for(TSource val : values)
		{
			TDest castVal;
			try
			{
				castVal = (TDest) destinationClass.cast(val);
			}
			catch(ClassCastException e)
			{
				// remove upon any failure
				continue;
			}

			result.add(castVal);
		}
		return result;
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * discarding elements that are not cast successfully.
	 */
	private static <TSource, TDest> void castRemove(TSource[] values, ReifiedList<TDest> list)
	{
		Class<?> destinationClass = list.getGenericTypeParameter();
		for(TSource val : values)
		{
			TDest castVal;
			try
			{
				castVal = (TDest) destinationClass.cast(val);
			}
			catch(ClassCastException e)
			{
				// remove upon any failure
				continue;
			}

			list.add(castVal);
		}
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * using nulls for elements that are not cast successfully
	 */
	private static <TSource, TDest> ReifiedList<TDest> castUseDefault(Iterable<TSource> values, Class<TDest> destinationClass)
	{
		ReifiedList<TDest> result = new ReifiedArrayList<TDest>(DEFAULT_LIST_SIZE, destinationClass);

		for(TSource val : values)
		{
			TDest castVal;
			try
			{
				castVal = (TDest) destinationClass.cast(val);
			}
			catch(ClassCastException e)
			{
				castVal = null;
			}

			result.add(castVal);
		}

		return result;
	}

	/**
	 * Casts a sequence of values of a certain type to an array of values of another type,
	 * using nulls for elements that are not cast successfully
	 *
	 * @throws NullPointerException An argument is null.
	 */
	private static <TSource, TDest> void castUseDefault(TSource[] values, ReifiedList<TDest> list)
	{
		Class<?> destinationClass = list.getGenericTypeParameter();

		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			TDest castVal;
			try
			{
				castVal = (TDest) destinationClass.cast(values[i]);
			}
			catch(ClassCastException e)
			{
				castVal = null;
			}
			// add
			list.add(castVal);
		}
	}

	/**
	 * Returns true if a non-null item is contained in the sequence of values
	 */
	private static <T> boolean containsNonNull(Iterable<T> values, T item)
	{
		for(T val : values)
			// if a value is null, we cannot use equals
			if(val != null)
				if(val.equals(item))
					return true;

		return false;
	}

	/**
	 * Returns true if a non-null item is contained in the sequence of values
	 */
	private static <T> boolean containsNonNull(Iterable<T> values, T item, Comparator<? super T> comparer)
	{
		for(T val : values)
// if a value is null, we cannot use equals
			if(val != null)
				if(comparer.compare(val, item) == 0)
					return true;

		return false;
	}

	/**
	 * Returns true if null is contained in the sequence of values
	 */
	private static <T> boolean containsNull(Iterable<T> values)
	{
		for(T item : values)
			if(item == null)
				return true;

		return false;
	}

	/**
	 * Returns true if a non-null item is contained in the sequence of values
	 */
	private static <T> boolean containsNonNull(T[] values, T item)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T val = values[i];
// if a value is null, we cannot use equals
			if(val != null)
				if(val.equals(item))
					return true;
		}

		return false;
	}

	/**
	 * Returns true if a non-null item is contained in the sequence of values
	 */
	private static <T> boolean containsNonNull(T[] values, T item, Comparator<? super T> comparer)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T val = values[i];
// if a value is null, we cannot use equals
			if(val != null)
				if(comparer.compare(val, item) == 0)
					return true;
		}

		return false;
	}

	/**
	 * Returns true if null is contained in the sequence of values
	 */
	private static <T> boolean containsNull(T[] values)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
			if(values[i] == null)
				return true;

		return false;
	}

	/**
	 * Returns the number of null entries in the sequence
	 */
	private static <T> int countNulls(Iterable<? extends T> values)
	{
// check if value contained in sequence
		int result = 0;

// the values is confirmed to be a non-null IEnumerable prior to this
		for(T val : values)
			if(val == null)
				result++;

		return result;
	}

	/**
	 * Returns the number of null entries in the sequence
	 */
	private static <T> int countNulls(T[] values)
	{
// check if value contained in sequence
		int result = 0;

// the values is confirmed to be a non-null IEnumerable prior to this
		int count = values.length;
		for(int i = 0; i < count; i++)
			if(values[i] == null)
				result++;

		return result;
	}

	/**
	 * Returns the number of occurences of a non-null value in the collection
	 */
	private static <T> int countNonNull(Iterable<T> values, T value)
	{
		// check if value contained collection
		int result = 0;

		// the values is confirmed to be a non-null IEnumerable prior to this
		for(T val : values)
			if(val != null)
				if(val.equals(value))
					result++;

		return result;
	}

	/**
	 * Returns the number of occurences of a non-null value in the collection
	 */
	private static <T> int countNonNull(Iterable<T> values, T value, Comparator<? super T> comparer)
	{
		// check if value contained collection
		int result = 0;

		// the values is confirmed to be a non-null IEnumerable prior to this
		for(T val : values)
			if(val != null)
				if(comparer.compare(val, value) == 0)
					result++;

		return result;
	}

	/**
	 * Returns the number of occurences of a non-null value in the collection
	 */
	private static <T> int countNonNull(T[] values, T value)
	{
		// check if value contained collection
		int result = 0;

		// the values is confirmed to be a non-null IEnumerable prior to this
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T val = values[i];
			if(val != null)
				if(val.equals(value))
					result++;
		}

		return result;
	}

	/**
	 * Returns the number of occurences of a non-null value in the collection
	 */
	private static <T> int countNonNull(T[] values, T value, Comparator<? super T> comparer)
	{
		// check if value contained collection
		int result = 0;

		// the values is confirmed to be a non-null IEnumerable prior to this
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			T val = values[i];
			if(val != null)
				if(comparer.compare(val, value) == 0)
					result++;
		}

		return result;
	}

	/**
	 * Returns the index where the first null element is found.
	 * If no null elements are found, this returns -1.
	 */
	private static <T> int indexOfNull(Iterable<T> values)
	{
		int i = 0;
		for(T item : values)
		{
			if(item == null)
				return i;
			i++;
		}

		return -1;
	}

	/**
	 * Returns the index where the first null element is found.
	 * If no null elements are found, this returns -1.
	 */
	private static <T> int indexOfNull(T[] values)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			if(values[i] == null)
				return i;
		}

		return -1;
	}

	/**
	 * Returns the index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int indexOfNotNull(Iterable<? super T> values, T element)
	{
		int i = 0;
		for(Object item : values)
		{
			if(element.equals(item))
				return i;
			i++;
		}

		return -1;
	}

	/**
	 * Returns the index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int indexOfNotNull(Iterable<T> values, T element, Comparator<? super T> comparer)
	{
		int i = 0;
		for(T item : values)
		{
			if(comparer.compare(element, item) == 0)
				return i;
			i++;
		}

		return -1;
	}

	/**
	 * Returns the index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int indexOfNotNull(T[] values, T element)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			if(element.equals(values[i]))
				return i;
		}

		return -1;
	}

	/**
	 * Returns the index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int indexOfNotNull(T[] values, T element, Comparator<? super T> comparer)
	{
		int count = values.length;
		for(int i = 0; i < count; i++)
		{
			if(comparer.compare(element, values[i]) == 0)
				return i;
		}

		return -1;
	}

	/**
	 * Returns the last index where the first null element is found.
	 * If no null elements are found, this returns -1.
	 */
	private static <T> int lastIndexOfNull(Iterable<T> values)
	{
		int lastPos = -1;
		int i = 0;
		for(T item : values)
		{
			if(item == null)
				lastPos = i;
			i++;
		}

		return lastPos;
	}

	/**
	 * Returns the last index where the first null element is found.
	 * If no null elements are found, this returns -1.
	 */
	private static <T> int lastIndexOfNull(T[] values)
	{
		int count = values.length;
		for(int i = count; i >= 0; i--)
		{
			if(values[i] == null)
				return i;
		}

		return -1;
	}

	/**
	 * Returns the last index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int lastIndexOfNotNull(Iterable<? super T> values, T element)
	{
		int lastPos = -1;
		int i = 0;
		for(Object item : values)
		{
			if(element.equals(item))
				lastPos = i;
			i++;
		}

		return lastPos;
	}

	/**
	 * Returns the last index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int lastIndexOfNotNull(Iterable<T> values, T element, Comparator<? super T> comparer)
	{
		int lastPos = -1;
		int i = 0;
		for(T item : values)
		{
			if(comparer.compare(element, item) == 0)
				lastPos = i;
			i++;
		}

		return lastPos;
	}

	/**
	 * Returns the last index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int lastIndexOfNotNull(T[] values, T element)
	{
		int count = values.length;
		for(int i = count - 1; i >= 0; i--)
		{
			if(element.equals(values[i]))
				return i;
		}

		return -1;
	}

	/**
	 * Returns the last index where the specified not-null element is first found.
	 * If the element is not found, this returns -1.
	 */
	private static <T> int lastIndexOfNotNull(T[] values, T element, Comparator<? super T> comparer)
	{
		int count = values.length;
		for(int i = count - 1; i >= 0; i--)
		{
			if(comparer.compare(element, values[i]) == 0)
				return i;
		}

		return -1;
	}
}
