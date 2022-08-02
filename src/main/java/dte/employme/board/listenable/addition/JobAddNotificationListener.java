package dte.employme.board.listenable.addition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.services.addnotifiers.JobAddedNotifierService;

public class JobAddNotificationListener implements JobAddListener
{
	private final JobAddedNotifierService jobAddedNotifierService;

	public JobAddNotificationListener(JobAddedNotifierService jobAddedNotifierService) 
	{
		this.jobAddedNotifierService = jobAddedNotifierService;
	}

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		this.jobAddedNotifierService.getPlayersNotifiers().forEach((playerUUID, playerNotifier) -> 
		{
			Player player = Bukkit.getPlayer(playerUUID);

			if(player.getUniqueId().equals(job.getEmployer().getUniqueId()))
				return;

			if(!playerNotifier.shouldNotify(player, job))
				return;

			playerNotifier.notify(player, job);
		});
	}
}