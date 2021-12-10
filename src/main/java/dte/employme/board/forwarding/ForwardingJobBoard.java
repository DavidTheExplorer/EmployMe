package dte.employme.board.forwarding;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

public abstract class ForwardingJobBoard implements JobBoard
{
	protected final JobBoard delegate;
	
	protected ForwardingJobBoard(JobBoard delegate) 
	{
		this.delegate = delegate;
	}
	
	@Override
	public void addJob(Job job)
	{
		this.delegate.addJob(job);
	}

	@Override
	public void removeJob(Job job)
	{
		this.delegate.removeJob(job);
	}

	@Override
	public void completeJob(Job job, Player whoCompleted) 
	{
		this.delegate.completeJob(job, whoCompleted);
	}

	@Override
	public List<Job> getOfferedJobs() 
	{
		return this.delegate.getOfferedJobs();
	}

	@Override
	public List<Job> getJobsOfferedBy(UUID employerUUID) 
	{
		return this.delegate.getJobsOfferedBy(employerUUID);
	}

	@Override
	public Optional<Job> getJobByID(String id) 
	{
		return this.delegate.getJobByID(id);
	}

	@Override
	public Optional<String> getJobID(Job job) 
	{
		return this.delegate.getJobID(job);
	}
	
	@Override
	public Iterator<Job> iterator() 
	{
		return this.delegate.iterator();
	}
}