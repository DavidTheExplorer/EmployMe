package dte.employme.services.addnotifiers;

import java.util.Set;
import java.util.UUID;

import dte.employme.addednotifiers.JobAddedNotifier;

public interface JobAddedNotifierService
{
	JobAddedNotifier getByName(String name);
	Set<JobAddedNotifier> getNotifiers();
	void register(JobAddedNotifier notifier);
	
	JobAddedNotifier getPlayerNotifier(UUID playerUUID);
	void setPlayerNotifier(UUID playerUUID, JobAddedNotifier notifier);
	
	void loadPlayersNotifiers();
	void savePlayersNotifiers();
}