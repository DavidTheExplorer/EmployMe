package dte.employme.board.listeners.completion;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;

public class JobRewardGiveListener implements JobCompleteListener
{
	@Override
	public void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context) 
	{
		context.getReward().giveTo(whoCompleted);
	}
}