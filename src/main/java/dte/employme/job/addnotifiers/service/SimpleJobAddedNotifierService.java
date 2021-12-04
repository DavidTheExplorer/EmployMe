package dte.employme.job.addnotifiers.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dte.employme.config.ConfigFile;
import dte.employme.job.addnotifiers.DoNotNotify;
import dte.employme.job.addnotifiers.JobAddedNotifier;

public class SimpleJobAddedNotifierService implements JobAddedNotifierService
{
	private final Map<String, JobAddedNotifier> notifierByName = new HashMap<>();
	private final Map<UUID, JobAddedNotifier> playersNotifiers = new HashMap<>();
	private final ConfigFile notifiersConfig;
	
	private static final DoNotNotify DO_NOT_NOTIFY = new DoNotNotify();
	
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
	public JobAddedNotifier getPlayerNotifier(UUID playerUUID) 
	{
		return this.notifierByName.getOrDefault(playerUUID, DO_NOT_NOTIFY);
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
		this.notifiersConfig.save(IOException::printStackTrace);
	}
}
