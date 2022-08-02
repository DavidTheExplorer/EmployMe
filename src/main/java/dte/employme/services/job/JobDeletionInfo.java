package dte.employme.services.job;

import java.time.Duration;
import java.time.LocalDateTime;

import org.bukkit.scheduler.BukkitTask;

public class JobDeletionInfo
{
	private final LocalDateTime deletionDate;
	private final BukkitTask task;

	public JobDeletionInfo(Duration delay, BukkitTask task) 
	{
		this.deletionDate = LocalDateTime.now().plus(delay);
		this.task = task;
	}

	public LocalDateTime getDeletionDate()
	{
		return this.deletionDate;
	}

	public BukkitTask getTask() 
	{
		return this.task;
	}
}
