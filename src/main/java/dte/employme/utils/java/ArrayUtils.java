package dte.employme.utils.java;

import static dte.employme.utils.java.Predicates.negate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ArrayUtils
{
	//Container of static methods
	private ArrayUtils(){}

	@SafeVarargs
	public static <T, C extends Collection<T>> C to(Supplier<C> baseSupplier, T... array)
	{
		C collection = baseSupplier.get();

		collection.addAll(Arrays.asList(array));

		return collection;
	}
	
	public static <T> void reverse(T[] array)
	{
		for(int i = 0, halfLength = array.length/2; i < halfLength; i++)
			swap(array, i, array.length -1 -i);
	}
	
	public static <T> long countNulls(T[] array)
	{
		return Arrays.stream(array)
				.filter(negate(Objects::nonNull))
				.count();
	}
	
	public static <T> boolean isFull(T[] array)
	{
		return countNulls(array) == 0;
	}
	
	public static <T> boolean isEmpty(T[] array)
	{
		return countNulls(array) == array.length;
	}
	
	public static <T> void swap(T[] array, int index1, int index2) 
	{
		T temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
	
	public static <T> void shuffle(T[] array) 
	{
		for(int i = 0; i < array.length; i++)
			swap(array, i, ThreadLocalRandom.current().nextInt(array.length));
	}

	public enum From
	{
		START 
		{
			@Override
			public <T> int firstMatchingIndex(T[] array, Predicate<T> matcher)
			{
				for(int i = 0; i < array.length; i++)
				{
					if(matcher.test(array[i]))
						return i;
				}
				return -1;
			}
		}, 
		
		END
		{
			@Override
			public <T> int firstMatchingIndex(T[] array, Predicate<T> matcher)
			{
				for(int i = array.length-1; i >= 0; i--) 
				{
					if(matcher.test(array[i]))
						return i;
				}
				return -1;
			}
		};
		public abstract <T> int firstMatchingIndex(T[] array, Predicate<T> matcher);
	}
}