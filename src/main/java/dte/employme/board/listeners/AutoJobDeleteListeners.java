package dte.employme.board.listeners;

import java.time.Duration;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.board.listeners.addition.JobAddListener;
import dte.employme.board.listeners.completion.JobCompleteListener;
import dte.employme.board.listeners.removal.JobRemovalListener;
import dte.employme.job.Job;
import dte.employme.services.job.JobService;

public class AutoJobDeleteListeners implements JobAddListener, JobRemovalListener, JobCompleteListener
{
	private final Duration delay;
	private final JobService jobService;

	public AutoJobDeleteListeners(Duration delay, JobService jobService) 
	{
		this.delay = delay;
		this.jobService = jobService;
	}

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		this.jobService.deleteAfter(job, this.delay);
	}

	@Override
	public void onJobRemoved(JobBoard jobBoard, Job job) 
	{
		this.jobService.stopAutoDelete(job);
	}
	
	@Override
	public void onJobCompleted(JobBoard jobBoard, Job job, Player whoCompleted) 
	{
		this.jobService.stopAutoDelete(job);
	}
}