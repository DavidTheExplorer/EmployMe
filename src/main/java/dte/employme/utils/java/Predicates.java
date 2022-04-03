package dte.employme.utils.java;

import java.util.function.Predicate;

public class Predicates
{
	public static <T> Predicate<T> of(Predicate<T> predicate)
	{
		return predicate;
	}

	//a depressing method
	public static <T> Predicate<T> negate(Predicate<T> predicate)
	{
		return happyObject -> !predicate.test(happyObject);
	}
}