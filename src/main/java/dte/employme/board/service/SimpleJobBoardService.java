package dte.employme.board.service;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.Message;

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
			Message.sendGeneralMessage(employer, Message.JOB_ADDED_TO_BOARD);
	}
}