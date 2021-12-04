package dte.employme.job.service;

import java.io.IOException;

import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.job.Job;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private ConfigFile jobsConfig;

	public SimpleJobService(JobBoard globalJobBoard, ConfigFile jobsConfig) 
	{
		this.globalJobBoard = globalJobBoard;
		this.jobsConfig = jobsConfig;
	}

	@Override
	public void loadJobs() 
	{
		this.jobsConfig.getList("Jobs", Job.class).forEach(this.globalJobBoard::addJob);
	}
	
	@Override
	public void saveJobs() 
	{
		this.jobsConfig.getConfig().set("Jobs", this.globalJobBoard.getOfferedJobs());
		this.jobsConfig.save(IOException::printStackTrace);
	}
}