package dte.employme.job;

import org.bukkit.OfflinePlayer;

import dte.employme.goal.Goal;
import dte.employme.reward.Reward;

public interface Job
{
	OfflinePlayer getEmployer();
	Goal getGoal();
	Reward getReward();
}