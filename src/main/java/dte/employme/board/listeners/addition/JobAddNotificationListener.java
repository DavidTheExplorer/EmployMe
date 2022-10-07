package dte.employme.board.listeners.addition;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.employme.addednotifiers.JobAddedNotifier;
import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.services.addnotifiers.JobAddedNotifierService;

public class JobAddNotificationListener implements JobAddListener
{
	private final JobAddedNotifierService jobAddedNotifierService;
	private final JobAddedNotifier defaultNotifier;

	public JobAddNotificationListener(JobAddedNotifierService jobAddedNotifierService, JobAddedNotifier defaultNotifier) 
	{
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.defaultNotifier = defaultNotifier;
	}

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		UUID employerUUID = job.getEmployer().getUniqueId();
		
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			//don't notify the employer - they aren't stupid
			if(player.getUniqueId().equals(employerUUID))
				continue;
			
			JobAddedNotifier playerNotifier = this.jobAddedNotifierService.getPlayerNotifier(player.getUniqueId(), this.defaultNotifier);
			
			if(!playerNotifier.shouldNotify(player, job))
				continue;

			playerNotifier.notify(player, job);
		}
	}
}