package dte.employme.board.listeners;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.Message;

public class EmployerNotificationListener implements JobAddListener
{
	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		Message.sendGeneralMessage(job.getEmployer().getPlayer(), Message.JOB_ADDED_TO_BOARD);
	}
}