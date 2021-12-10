package dte.employme.board.listenable;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

public interface ListenableJobBoard extends JobBoard
{
	void registerAddListener(JobAddListener... listeners);
	void registerCompleteListener(JobCompleteListener... listeners);
	
	
	
	@FunctionalInterface
	public interface JobAddListener
	{
		void onJobAdded(JobBoard jobBoard, Job job);
	}
	
	@FunctionalInterface
	public interface JobCompleteListener
	{
		void onJobCompleted(JobBoard jobBoard, Job job, Player whoCompleted);
	}
}