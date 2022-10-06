package dte.employme.board.listeners.completion;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;

@FunctionalInterface
public interface JobCompleteListener
{
	void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context);
}