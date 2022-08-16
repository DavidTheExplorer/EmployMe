package dte.employme.services.job;

import java.time.Duration;

import dte.employme.job.Job;

public interface JobService 
{
	String describeInGame(Job job);
	
	void loadJobs();
	void saveJobs();
	
	void deleteAfter(Job job, Duration delay);
	void stopAutoDelete(Job job);
	void loadAutoDeletionData();
	void saveAutoDeletionData();
}