package dte.employme.job.addnotifiers;

public abstract class AbstractJobAddedNotifier implements JobAddedNotifier
{
	private final String name;
	
	protected AbstractJobAddedNotifier(String name) 
	{
		this.name = name;
	}
	
	@Override
	public String getName() 
	{
		return this.name;
	}
}
