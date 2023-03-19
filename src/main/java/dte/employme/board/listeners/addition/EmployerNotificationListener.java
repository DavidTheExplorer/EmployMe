package dte.employme.board.listeners.addition;

import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;
import dte.employme.utils.OfflinePlayerUtils;

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
		OfflinePlayerUtils.ifOnline(job.getEmployer(), this.messageService.loadMessage(JOB_ADDED_TO_BOARD)::sendTo);
	}
}