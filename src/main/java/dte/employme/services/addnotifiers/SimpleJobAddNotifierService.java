package dte.employme.services.addnotifiers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dte.employme.addnotifiers.JobAddNotifier;
import dte.spigotconfiguration.SpigotConfig;

public class SimpleJobAddNotifierService implements JobAddNotifierService
{
	private final Map<String, JobAddNotifier> notifierByName = new HashMap<>();
	private final Map<UUID, JobAddNotifier> playersNotifiers = new HashMap<>();
	private final SpigotConfig notifiersConfig;
	
	public SimpleJobAddNotifierService(SpigotConfig notifiersConfig) 
	{
		this.notifiersConfig = notifiersConfig;
	}
	
	@Override
	public JobAddNotifier getByName(String name) 
	{
		return this.notifierByName.get(name.toLowerCase());
	}
	
	@Override
	public void register(JobAddNotifier notifier) 
	{
		this.notifierByName.put(notifier.getName().toLowerCase(), notifier);
	}
	
	@Override
	public Set<JobAddNotifier> getNotifiers() 
	{
		return new HashSet<>(this.notifierByName.values());
	}

	@Override
	public JobAddNotifier getPlayerNotifier(UUID playerUUID, JobAddNotifier defaultNotifier) 
	{
		return this.playersNotifiers.getOrDefault(playerUUID, defaultNotifier);
	}

	@Override
	public void setPlayerNotifier(UUID playerUUID, JobAddNotifier notifier) 
	{
		this.playersNotifiers.put(playerUUID, notifier);
	}

	@Override
	public void loadPlayersNotifiers()
	{
		this.notifiersConfig.getValues(false).forEach((uuidString, policyName) -> 
		{
			UUID playerUUID = UUID.fromString(uuidString);
			JobAddNotifier playerNotifier = getByName((String) policyName);
			
			setPlayerNotifier(playerUUID, playerNotifier);
		});
	}

	@Override
	public void savePlayersNotifiers() 
	{
		this.playersNotifiers.forEach((playerUUID, playerPolicy) -> this.notifiersConfig.set(playerUUID.toString(), playerPolicy.getName()));
		
		try 
		{
			this.notifiersConfig.save();
		} 
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
