package dte.employme.job.addnotifiers;

import org.bukkit.entity.Player;

import dte.employme.job.Job;

public interface JobAddedNotifier
{
	String getName();
	boolean shouldNotify(Player player, Job job);
	void notify(Player player, Job job);
}