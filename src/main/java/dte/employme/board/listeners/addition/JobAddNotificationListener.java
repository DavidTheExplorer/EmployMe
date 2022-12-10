package dte.employme.board.listeners.addition;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.employme.addnotifiers.JobAddNotifier;
import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.services.addnotifiers.JobAddNotifierService;

public class JobAddNotificationListener implements JobAddListener
{
	private final JobAddNotifierService jobAddNotifierService;
	private final JobAddNotifier defaultNotifier;

	public JobAddNotificationListener(JobAddNotifierService jobAddNotifierService, JobAddNotifier defaultNotifier) 
	{
		this.jobAddNotifierService = jobAddNotifierService;
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
			
			JobAddNotifier playerNotifier = this.jobAddNotifierService.getPlayerNotifier(player.getUniqueId(), this.defaultNotifier);
			
			if(!playerNotifier.shouldNotify(player, job))
				continue;

			playerNotifier.notify(player, job);
		}
	}
}