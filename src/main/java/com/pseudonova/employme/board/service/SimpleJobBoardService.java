package com.pseudonova.employme.board.service;

import org.bukkit.entity.Player;

import com.pseudonova.employme.board.JobBoard;
import com.pseudonova.employme.job.Job;
import com.pseudonova.employme.messages.Message;
import com.pseudonova.employme.reward.visitor.RewardNameVisitor;

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
			Message.JOB_ADDED_TO_BOARD.sendTo(employer, job.getReward().accept(RewardNameVisitor.INSTANCE));
	}
}