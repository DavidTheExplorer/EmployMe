package dte.employme.board.listenable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.board.listenable.ListenableJobBoard.JobAddListener;
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

			if(playerNotifier.shouldNotify(player, job))
				playerNotifier.notify(player, job);
		});
	}
}