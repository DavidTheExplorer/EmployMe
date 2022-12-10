package dte.employme.configs;

import org.bukkit.plugin.Plugin;

import dte.employme.job.addnotifiers.JobAddNotifier;
import dte.employme.services.job.addnotifiers.JobAddNotifierService;
import dte.spigotconfiguration.SpigotConfig;
import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class MainConfig extends SpigotConfig
{
	public MainConfig(Plugin plugin) throws ConfigLoadException
	{
		super(new Builder(plugin).fromInternalResource("config"));
	}
	
	public JobAddNotifier parseDefaultAddNotifier(JobAddNotifierService jobAddNotifierService) 
	{
		String notifierName = getString("Default Job Add Notifier");
		JobAddNotifier notifier = jobAddNotifierService.getByName(notifierName);
		
		if(notifier == null)
			throw new RuntimeException(String.format("The default job add notifier '%s' could not be found!", notifierName));
		
		return notifier;
	}
	
	public int getMaxAllowedJobs(String groupName, int defaultAmount) 
	{
		return getSection("Maximum Allowed Jobs").getInt(groupName.toLowerCase(), defaultAmount);
	}
}
