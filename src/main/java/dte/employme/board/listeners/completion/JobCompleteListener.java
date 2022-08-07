package dte.employme.board.listeners.completion;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

@FunctionalInterface
public interface JobCompleteListener
{
	void onJobCompleted(JobBoard jobBoard, Job job, Player whoCompleted);
}