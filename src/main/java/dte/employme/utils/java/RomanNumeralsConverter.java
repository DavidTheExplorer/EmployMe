package dte.employme.utils.java;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class RomanNumeralsConverter 
{
	private static final Map<String, Integer> ROMAN_NUMERALS = new MapBuilder<String, Integer>()
			.put("M", 1000)
			.put("CM", 900)
			.put("D", 500)
			.put("CD", 400)
			.put("C", 100)
			.put("XC", 90)
			.put("L", 50)
			.put("XL", 40)
			.put("X", 10)
			.put("IX", 9)
			.put("V", 5)
			.put("IV", 4)
			.put("I", 1)
			.buildTo(new LinkedHashMap<>());

	public static String convert(int number) 
	{
		StringBuilder result = new StringBuilder();
		
		for(Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet())
		{
			int matches = (number / entry.getValue());
			result.append(StringUtils.repeat(entry.getKey(), matches));
			number %= entry.getValue();
		}
		return result.toString();
	}
}