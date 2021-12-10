package dte.employme.board.listenable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.board.forwarding.ForwardingJobBoard;
import dte.employme.job.Job;

public class SimpleListenableJobBoard extends ForwardingJobBoard implements ListenableJobBoard
{
	private final Set<JobAddListener> addListeners = new LinkedHashSet<>();
	private final Set<JobCompleteListener> completeListeners = new LinkedHashSet<>();
	
	public SimpleListenableJobBoard(JobBoard jobBoard) 
	{
		super(jobBoard);
	}
	
	@Override
	public void addJob(Job job)
	{
		super.addJob(job);
		
		this.addListeners.forEach(listener -> listener.onJobAdded(this, job));
	}
	
	@Override
	public void completeJob(Job job, Player whoCompleted) 
	{
		super.completeJob(job, whoCompleted);
		
		this.completeListeners.forEach(listener -> listener.onJobCompleted(this, job, whoCompleted));
	}
	
	@Override
	public void registerAddListener(JobAddListener... listeners) 
	{
		Arrays.stream(listeners).forEach(this.addListeners::add);
	}
	
	@Override
	public void registerCompleteListener(JobCompleteListener... listeners) 
	{
		Arrays.stream(listeners).forEach(this.completeListeners::add);
	}
}
