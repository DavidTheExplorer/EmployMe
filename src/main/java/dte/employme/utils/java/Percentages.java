package dte.employme.utils.java;

import java.text.DecimalFormat;

public class Percentages
{
	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");
	
	public static double of(double part, double whole)
	{
		return ((double) (part * 100) / whole);
	}
	
	public static String format(double percentage) 
	{
		return FORMATTER.format(percentage);
	}
	
	public static double toFraction(double percentage) 
	{
		return percentage / 100;
	}
	
	public static double toFormal(double percentage) 
	{
		return percentage * 100;
	}
}