package com.pseudonova.employme.utils;

import java.util.Optional;

public class NumberUtils 
{
	//Container of static methods
	private NumberUtils(){}

	public static boolean isBetween(double number, double startInclusive, double endExclusive) 
	{
		return number >= startInclusive && number < endExclusive;
	}

	public static Optional<Integer> parseInt(String text)
	{
		try 
		{
			return Optional.of(Integer.parseInt(text));
		}
		catch(NumberFormatException exception) 
		{
			return Optional.empty();
		}
	}

	public static Optional<Double> parseDouble(String text)
	{
		try 
		{
			return Optional.of(Double.parseDouble(text));
		}
		catch(NumberFormatException exception) 
		{
			return Optional.empty();
		}
	}
}