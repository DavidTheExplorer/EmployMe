package dte.employme.board.listenable.completion;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

public class JobRewardGiveListener implements JobCompleteListener
{
	@Override
	public void onJobCompleted(JobBoard board, Job job, Player whoCompleted) 
	{
		job.getReward().giveTo(whoCompleted);
	}
}