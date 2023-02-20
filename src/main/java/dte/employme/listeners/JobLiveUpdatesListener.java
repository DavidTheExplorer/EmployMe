package dte.employme.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dte.employme.services.job.JobService;

public class JobLiveUpdatesListener implements Listener 
{
	private final JobService jobService;

	public JobLiveUpdatesListener(JobService jobService)
	{
		this.jobService = jobService;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) 
	{
		this.jobService.stopLiveUpdates(event.getPlayer());
	}
}
