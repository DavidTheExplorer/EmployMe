package dte.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public class SimpleJobBoard implements JobBoard
{
	private final List<Job> jobs = new ArrayList<>();
	
	@Override
	public void addJob(Job job) 
	{
		this.jobs.add(job);
	}
	
	@Override
	public void removeJob(Job job) 
	{
		this.jobs.remove(job);
	}
	
	@Override
	public void completeJob(Job job, Player whoCompleted) 
	{
		removeJob(job);
	}
	
	@Override
	public List<Job> getOfferedJobs()
	{
		return new ArrayList<>(this.jobs);
	}
	
	@Override
	public List<Job> getJobsOfferedBy(UUID employerUUID) 
	{
		return this.jobs.stream()
				.filter(job -> job.getEmployer().getUniqueId().equals(employerUUID))
				.collect(toList());
	}
	
	@Override
	public Iterator<Job> iterator() 
	{
		return this.jobs.iterator();
	}
}