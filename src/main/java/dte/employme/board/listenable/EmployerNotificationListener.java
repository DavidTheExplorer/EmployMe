package dte.employme.board.listenable;

import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;
import static dte.employme.messages.MessageKey.PREFIX;

import dte.employme.board.JobBoard;
import dte.employme.board.listenable.ListenableJobBoard.JobAddListener;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;

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
		this.messageService.getMessage(JOB_ADDED_TO_BOARD)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.sendIfOnline(job.getEmployer());
	}
}