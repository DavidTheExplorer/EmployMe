package dte.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public class SimpleJobBoard implements JobBoard
{
	private final Map<UUID, Job> jobByUUID = new HashMap<>();
	
	@Override
	public void addJob(Job job) 
	{
		this.jobByUUID.put(job.getUUID(), job);
	}
	
	@Override
	public void removeJob(Job job) 
	{
		this.jobByUUID.remove(job.getUUID());
	}
	
	@Override
	public void completeJob(Job job, Player whoCompleted) 
	{
		removeJob(job);
	}
	
	@Override
	public List<Job> getOfferedJobs()
	{
		return new ArrayList<>(this.jobByUUID.values());
	}
	
	@Override
	public Optional<Job> getJobByUUID(UUID uuid) 
	{
		return Optional.ofNullable(this.jobByUUID.get(uuid));
	}
	
	@Override
	public List<Job> getJobsOfferedBy(UUID employerUUID) 
	{
		return this.jobByUUID.values().stream()
				.filter(job -> job.getEmployer().getUniqueId().equals(employerUUID))
				.collect(toList());
	}
	
	@Override
	public Iterator<Job> iterator() 
	{
		return this.jobByUUID.values().iterator();
	}
}