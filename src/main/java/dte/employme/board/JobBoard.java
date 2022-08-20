package dte.employme.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import dte.employme.board.listeners.addition.JobAddListener;
import dte.employme.board.listeners.completion.JobCompleteListener;
import dte.employme.board.listeners.removal.JobRemovalListener;
import dte.employme.job.Job;

public interface JobBoard extends Iterable<Job>
{
	void addJob(Job job);
	void removeJob(Job job);
	
	default void completeJob(Job job, Player whoCompleted) 
	{
		removeJob(job);
	}

	//query
	Optional<Job> getJobByUUID(UUID uuid);
	List<Job> getJobsOfferedBy(UUID employerUUID);
	List<Job> getOfferedJobs();

	//listeners
	void registerAddListener(JobAddListener... listeners);
	void registerCompleteListener(JobCompleteListener... listeners);
	void registerRemovalListener(JobRemovalListener... listeners);
	void removeAddListener(JobAddListener... listeners);
	void removeCompleteListener(JobCompleteListener... listeners);
	void removeRemovalListener(JobRemovalListener... listeners);
}