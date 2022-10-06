package dte.employme.services.job;

import java.time.Duration;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;
import dte.employme.services.rewards.PartialCompletionInfo;

public interface JobService 
{
	FinishState getFinishState(Player player, Job job);
	String describeCompletionInGame(Job job, JobCompletionContext context);
	
	PartialCompletionInfo getPartialCompletionInfo(Player player, Job job);
	String describeInGame(Job job);
	
	void loadJobs();
	void saveJobs();
	
	void deleteAfter(Job job, Duration delay);
	void stopAutoDelete(Job job);
	void loadAutoDeletionData();
	void saveAutoDeletionData();
	
	
	
	enum FinishState
	{
		NEGATIVE, PARTIALLY, FULLY;
		
		public boolean hasFinished() 
		{
			return this != NEGATIVE;
		}
	}
}