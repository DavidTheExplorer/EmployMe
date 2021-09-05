package dte.employme.board.service;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.Message;
import dte.employme.reward.visitor.RewardNameVisitor;

public class SimpleJobBoardService implements JobBoardService
{
	@Override
	public void addJob(JobBoard jobBoard, Job job) 
	{
		jobBoard.addJob(job);
		
		notifyAdded(job);
	}
	
	private void notifyAdded(Job job) 
	{
		Player employer = job.getEmployer().getPlayer();
		
		if(employer != null) 
		{
			String rewardName = job.getReward().accept(RewardNameVisitor.INSTANCE);
			Message.sendGeneralMessage(employer, Message.JOB_ADDED_TO_BOARD, rewardName);
		}
	}
}