package dte.employme.board.listenable.removal;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

@FunctionalInterface
public interface JobRemovalListener 
{
	void onJobRemoved(JobBoard jobBoard, Job job);
}