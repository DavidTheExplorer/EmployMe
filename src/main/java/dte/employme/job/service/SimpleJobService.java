package dte.employme.job.service;

import java.io.IOException;

import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.job.Job;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private ConfigFile jobsConfig;

	public SimpleJobService(JobBoard globalJobBoard) 
	{
		this.globalJobBoard = globalJobBoard;
	}

	@Override
	public void loadJobs() 
	{
		this.jobsConfig = ConfigFile.byPath("jobs.yml");
		this.jobsConfig.createIfAbsent(IOException::printStackTrace);
		
		//loads all jobs from the config into the global board
		this.jobsConfig.getList("Jobs", Job.class).forEach(this.globalJobBoard::addJob);
	}
	
	@Override
	public void saveJobs() 
	{
		//saves all jobs to the YML config
		this.jobsConfig.getConfig().set("Jobs", this.globalJobBoard.getOfferedJobs());
		
		this.jobsConfig.save(IOException::printStackTrace);
	}
}