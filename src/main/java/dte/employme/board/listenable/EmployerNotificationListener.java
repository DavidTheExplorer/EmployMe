package dte.employme.board.listenable;

import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;

import dte.employme.board.JobBoard;
import dte.employme.board.listenable.ListenableJobBoard.JobAddListener;
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
		job.getEmployer().getPlayer().sendMessage(this.messageService.getGeneralMessage(JOB_ADDED_TO_BOARD));
	}
}