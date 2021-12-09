package dte.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dte.employme.board.listeners.JobAddListener;
import dte.employme.board.listeners.JobCompleteListener;
import dte.employme.job.Job;

public class SimpleJobBoard implements JobBoard
{
	private final BiMap<String, Job> jobByID = HashBiMap.create();
	
	//listeners
	private final Set<JobAddListener> addListeners = new LinkedHashSet<>();
	private final Set<JobCompleteListener> completeListeners = new LinkedHashSet<>();

	@Override
	public void addJob(Job job) 
	{
		this.jobByID.put(generateID(), job);
		this.addListeners.forEach(listener -> listener.onJobAdded(this, job));
	}
	
	@Override
	public void removeJob(Job job) 
	{
		this.jobByID.inverse().remove(job);
	}
	
	@Override
	public void completeJob(Job job, Player whoCompleted) 
	{
		removeJob(job);
		
		this.completeListeners.forEach(listener -> listener.onJobCompleted(this, job, whoCompleted));
	}
	
	@Override
	public List<Job> getOfferedJobs()
	{
		return new ArrayList<>(this.jobByID.values());
	}
	
	@Override
	public List<Job> getJobsOfferedBy(UUID employerUUID) 
	{
		return this.jobByID.values().stream()
				.filter(job -> job.getEmployer().getUniqueId().equals(employerUUID))
				.collect(toList());
	}
	
	@Override
	public Optional<Job> getJobByID(String id) 
	{
		return Optional.ofNullable(this.jobByID.get(id));
	}
	
	@Override
	public Optional<String> getJobID(Job job) 
	{
		return Optional.ofNullable(this.jobByID.inverse().get(job));
	}
	
	@Override
	public void registerAddListener(JobAddListener... listeners) 
	{
		//TODO: replace with Arrays.stream
		for(JobAddListener listener : listeners)
			this.addListeners.add(listener);
	}
	
	@Override
	public void registerCompleteListener(JobCompleteListener... listeners) 
	{
		//TODO: replace with Arrays.stream
		for(JobCompleteListener listener : listeners)
			this.completeListeners.add(listener);
	}
	
	@Override
	public Iterator<Job> iterator() 
	{
		return this.jobByID.values().iterator();
	}

	private String generateID()
	{
		String id;

		do 
		{
			id = RandomStringUtils.randomAlphanumeric(4);
		}
		while(this.jobByID.containsKey(id));

		return id;
	}
}