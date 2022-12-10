package dte.employme.addnotifiers;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public class DoNotNotify extends JobAddNotifier
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