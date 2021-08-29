package com.pseudonova.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.pseudonova.employme.job.Job;

public interface JobBoard extends Iterable<Job>
{
	void addJob(Job job);
	void removeJob(Job job);
	List<Job> getOfferedJobs();
	void showTo(Player player);
	
	default void onComplete(Job job, Player completer) 
	{
		removeJob(job);
		job.onComplete(completer);
	}
	
	default List<Job> getJobsOfferedBy(UUID employerUUID)
	{
		return getOfferedJobs().stream()
				.filter(job -> job.getEmployer().getUniqueId().equals(employerUUID))
				.collect(toList());
	}
	
	@Override
	default Iterator<Job> iterator() 
	{
		return getOfferedJobs().iterator();
	}
}