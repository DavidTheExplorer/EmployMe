package dte.employme.board.listeners;

import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.service.MessageService;

public class EmployerNotificationListener implements JobAddListener
{
	private final MessageService messageService;
	
	public EmployerNotificationListener(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		this.messageService.sendGeneralMessage(job.getEmployer().getPlayer(), JOB_ADDED_TO_BOARD);
	}
}