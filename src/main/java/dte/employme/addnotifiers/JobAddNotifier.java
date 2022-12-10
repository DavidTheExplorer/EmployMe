package dte.employme.addnotifiers;

import java.util.Objects;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public abstract class JobAddNotifier
{
	private final String name;
	
	protected JobAddNotifier(String name) 
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
		
		if(!(object instanceof JobAddNotifier))
			return false;
		
		JobAddNotifier other = (JobAddNotifier) object;
		
		return Objects.equals(this.name, other.name);
	}
	
	public abstract boolean shouldNotify(Player player, Job job);
	public abstract void notify(Player player, Job job);
}