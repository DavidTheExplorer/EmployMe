package dte.employme.job;

import org.bukkit.OfflinePlayer;

import dte.employme.job.goals.Goal;
import dte.employme.job.rewards.Reward;

public interface Job
{
	OfflinePlayer getEmployer();
	Goal getGoal();
	Reward getReward();
}