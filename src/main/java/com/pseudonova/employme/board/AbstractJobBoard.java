package com.pseudonova.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.pseudonova.employme.job.Job;

public abstract class AbstractJobBoard implements JobBoard
{
	protected final List<Job> offeredJobs = new ArrayList<>();

	@Override
	public void addJob(Job job) 
	{
		this.offeredJobs.add(job);
	}

	@Override
	public void removeJob(Job job) 
	{
		this.offeredJobs.remove(job);
	}

	@Override
	public List<Job> getOfferedJobs()
	{
		return new ArrayList<>(this.offeredJobs);
	}

	@Override
	public List<Job> getJobsOfferedBy(UUID employerUUID) 
	{
		return this.offeredJobs.stream()
				.filter(job -> job.getEmployer().getUniqueId().equals(employerUUID))
				.collect(toList());
	}
	
	@Override
	public void onComplete(Job job, Player completer) 
	{
		removeJob(job);
		job.onComplete(completer);
	}

	@Override
	public Iterator<Job> iterator() 
	{
		return this.offeredJobs.iterator();
	}
}