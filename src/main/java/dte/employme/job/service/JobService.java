package dte.employme.job.service;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public interface JobService 
{
	void loadJobs();
	void saveJobs();
	
	void onComplete(Job job, Player completer);
	boolean hasFinished(Job job, Player player);
}