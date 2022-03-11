package dte.employme.services.job.addnotifiers;

import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.job.addnotifiers.JobAddedNotifier;

public class DoNotNotify extends JobAddedNotifier
{
	public static final DoNotNotify INSTANCE = new DoNotNotify();
	
	public DoNotNotify() 
	{
		super("None");
	}
	
	@Override
	public boolean shouldNotify(Player player, Job job) 
	{
		return false;
	}

	@Override
	public void notify(Player player, Job job) 
	{
		
	}
}