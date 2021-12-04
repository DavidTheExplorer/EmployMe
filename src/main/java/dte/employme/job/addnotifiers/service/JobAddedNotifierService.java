package dte.employme.job.addnotifiers.service;

import java.util.Map;
import java.util.UUID;

import dte.employme.job.addnotifiers.JobAddedNotifier;

public interface JobAddedNotifierService
{
	//retrieval
	JobAddedNotifier getByName(String name);
	void register(JobAddedNotifier notifier);
	
	//players' data
	JobAddedNotifier getPlayerNotifier(UUID playerUUID);
	void setPlayerNotifier(UUID playerUUID, JobAddedNotifier notifier);
	Map<UUID, JobAddedNotifier> getPlayersNotifiers();
	
	//load & save
	void loadPlayersNotifiers();
	void savePlayersNotifiers();
}
