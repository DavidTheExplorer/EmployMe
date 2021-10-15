package dte.employme.job.service;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public interface JobService 
{
	void loadJobs();
	void saveJobs();
	
	boolean hasFinished(Job job, Player player);
}