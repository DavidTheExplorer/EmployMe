package com.pseudonova.employme.board;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public Iterator<Job> iterator() 
	{
		return this.offeredJobs.iterator();
	}
}