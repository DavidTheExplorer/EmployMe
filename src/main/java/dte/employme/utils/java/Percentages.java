package dte.employme.utils.java;

public class Percentages
{
	public static double of(double part, double whole)
	{
		return ((double) (part * 100) / whole);
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