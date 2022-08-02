package dte.employme.board.listenable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import dte.employme.board.ForwardingJobBoard;
import dte.employme.board.JobBoard;
import dte.employme.board.listenable.addition.JobAddListener;
import dte.employme.board.listenable.completion.JobCompleteListener;
import dte.employme.board.listenable.removal.JobRemovalListener;
import dte.employme.job.Job;

public class ListenableJobBoard extends ForwardingJobBoard
{
	private final Set<JobAddListener> addListeners = new LinkedHashSet<>();
	private final Set<JobCompleteListener> completeListeners = new LinkedHashSet<>();
	private final Set<JobRemovalListener> removalListeners = new LinkedHashSet<>();
	
	public ListenableJobBoard(JobBoard jobBoard) 
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
	public void removeJob(Job job) 
	{
		super.removeJob(job);
		
		this.removalListeners.forEach(listener -> listener.onJobRemoved(this, job));
	}
	
	public void registerAddListener(JobAddListener... listeners) 
	{
		Arrays.stream(listeners).forEach(this.addListeners::add);
	}
	
	public void registerCompleteListener(JobCompleteListener... listeners) 
	{
		Arrays.stream(listeners).forEach(this.completeListeners::add);
	}
	
	public void registerRemovalListener(JobRemovalListener... listeners) 
	{
		Arrays.stream(listeners).forEach(this.removalListeners::add);
	}
}