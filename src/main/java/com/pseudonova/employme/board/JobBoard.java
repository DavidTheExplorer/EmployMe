package com.pseudonova.employme.board;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.pseudonova.employme.job.Job;

public interface JobBoard extends Iterable<Job>
{
	//Jobs
	void addJob(Job job);
	void removeJob(Job job);
	List<Job> getOfferedJobs();
	List<Job> getJobsOfferedBy(UUID employerUUID);
	void onComplete(Job job, Player completer);
	
	//Visualization
	void showTo(Player player);
}