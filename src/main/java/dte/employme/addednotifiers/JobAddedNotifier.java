package dte.employme.addednotifiers;

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
	
	public abstract boolean shouldNotify(Player player, Job job);
	public abstract void notify(Player player, Job job);
}