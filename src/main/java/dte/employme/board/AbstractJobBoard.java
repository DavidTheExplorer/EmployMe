package dte.employme.board;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dte.employme.job.Job;

public abstract class AbstractJobBoard implements JobBoard
{
	private final BiMap<String, Job> jobByID = HashBiMap.create(); 

	@Override
	public void addJob(Job job) 
	{
		this.jobByID.put(generateID(), job);
	}

	@Override
	public void removeJob(Job job) 
	{
		this.jobByID.inverse().remove(job);
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
	public Iterator<Job> iterator() 
	{
		return this.jobByID.values().iterator();
	}
	
	protected String generateID()
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