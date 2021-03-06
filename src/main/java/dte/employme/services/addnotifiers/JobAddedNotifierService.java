package dte.employme.services.addnotifiers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dte.employme.addednotifiers.JobAddedNotifier;

public interface JobAddedNotifierService
{
	//retrieval
	JobAddedNotifier getByName(String name);
	void register(JobAddedNotifier notifier);
	
	//players
	JobAddedNotifier getPlayerNotifier(UUID playerUUID);
	void setPlayerNotifier(UUID playerUUID, JobAddedNotifier notifier);
	Map<UUID, JobAddedNotifier> getPlayersNotifiers();
	Set<JobAddedNotifier> getNotifiers();
	
	//load & save
	void loadPlayersNotifiers();
	void savePlayersNotifiers();
}
