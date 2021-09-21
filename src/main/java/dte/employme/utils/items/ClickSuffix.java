package dte.employme.utils.items;

public enum ClickSuffix
{
	LEFT("Left Click"),
	RIGHT("Right Click"),
	LEFT_FIRST("Left Click / Right Click"),
	RIGHT_FIRST("Right Click / Left Click");

	private final String suffix;

	ClickSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	@Override
	public String toString() 
	{
		return this.suffix;
	}
}
