package dte.employme.services.job.addnotifiers;

import java.util.Set;
import java.util.UUID;

import dte.employme.job.addnotifiers.JobAddNotifier;

public interface JobAddNotifierService
{
	JobAddNotifier getByName(String name);
	Set<JobAddNotifier> getNotifiers();
	void register(JobAddNotifier notifier);
	
	JobAddNotifier getPlayerNotifier(UUID playerUUID, JobAddNotifier defaultNotifier);
	void setPlayerNotifier(UUID playerUUID, JobAddNotifier notifier);
	
	void loadPlayersNotifiers();
	void savePlayersNotifiers();
}