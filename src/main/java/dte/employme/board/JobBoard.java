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
	
	//listeners
	void registerAddListener(JobAddListener... listeners);
	void registerCompleteListener(JobCompleteListener... listeners);
	void registerRemovalListener(JobRemovalListener... listeners);
	
	//query
	Optional<Job> getJobByUUID(UUID uuid);
	List<Job> getOfferedJobs();
	List<Job> getJobsOfferedBy(UUID employerUUID);
}