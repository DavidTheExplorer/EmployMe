package dte.employme.utils.java;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;

public class StringUtils 
{
	public static String repeat(String text, int times) 
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 1; i <= times; i++) 
			builder.append(text);
		
		return builder.toString();
	}
	
	public static String capitalizeFully(String text) 
	{
		return Arrays.stream(text.split(" "))
				.map(word -> String.valueOf(Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()))
				.collect(joining(" "));
	}
}
