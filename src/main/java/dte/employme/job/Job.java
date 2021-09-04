package dte.employme.job;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import dte.employme.goal.Goal;
import dte.employme.reward.Reward;

public interface Job
{
	OfflinePlayer getEmployer();
	Goal getGoal();
	Reward getReward();
	boolean hasFinished(Player player);
	void onComplete(Player completer);
}