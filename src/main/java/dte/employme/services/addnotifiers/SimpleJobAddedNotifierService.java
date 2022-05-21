package dte.employme.services.addnotifiers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dte.employme.addednotifiers.DoNotNotify;
import dte.employme.addednotifiers.JobAddedNotifier;
import dte.employme.config.ConfigFile;

public class SimpleJobAddedNotifierService implements JobAddedNotifierService
{
	private final Map<String, JobAddedNotifier> notifierByName = new HashMap<>();
	private final Map<UUID, JobAddedNotifier> playersNotifiers = new HashMap<>();
	private final ConfigFile notifiersConfig;
	
	public SimpleJobAddedNotifierService(ConfigFile notifiersConfig) 
	{
		this.notifiersConfig = notifiersConfig;
	}
	
	@Override
	public JobAddedNotifier getByName(String name) 
	{
		return this.notifierByName.get(name.toLowerCase());
	}
	
	@Override
	public void register(JobAddedNotifier notifier) 
	{
		this.notifierByName.put(notifier.getName().toLowerCase(), notifier);
	}
	
	@Override
	public Set<JobAddedNotifier> getNotifiers() 
	{
		return new HashSet<>(this.notifierByName.values());
	}

	@Override
	public JobAddedNotifier getPlayerNotifier(UUID playerUUID) 
	{
		return this.playersNotifiers.getOrDefault(playerUUID, DoNotNotify.INSTANCE);
	}

	@Override
	public void setPlayerNotifier(UUID playerUUID, JobAddedNotifier notifier) 
	{
		this.playersNotifiers.put(playerUUID, notifier);
	}
	
	@Override
	public Map<UUID, JobAddedNotifier> getPlayersNotifiers() 
	{
		return new HashMap<>(this.playersNotifiers);
	}

	@Override
	public void loadPlayersNotifiers() 
	{
		this.notifiersConfig.getConfig().getValues(false).forEach((uuidString, policyName) -> 
		{
			UUID playerUUID = UUID.fromString(uuidString);
			JobAddedNotifier playerNotifier = getByName((String) policyName);
			
			setPlayerNotifier(playerUUID, playerNotifier);
		});
	}

	@Override
	public void savePlayersNotifiers() 
	{
		this.playersNotifiers.forEach((playerUUID, playerPolicy) -> this.notifiersConfig.getConfig().set(playerUUID.toString(), playerPolicy.getName()));
		
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
