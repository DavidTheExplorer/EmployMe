package dte.employme.addednotifiers;

import java.util.Objects;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public abstract class JobAddedNotifier
{
	private final String name;
	
	protected JobAddedNotifier(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return this.name;
	}
	
	public int hashCode() 
	{
		return Objects.hash(this.name);
	}

	@Override
	public boolean equals(Object object)
	{
		if(this == object)
			return true;
		
		if(!(object instanceof JobAddedNotifier))
			return false;
		
		JobAddedNotifier other = (JobAddedNotifier) object;
		
		return Objects.equals(this.name, other.name);
	}
	
	public abstract boolean shouldNotify(Player player, Job job);
	public abstract void notify(Player player, Job job);
}