package com.pseudonova.employme.job;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.pseudonova.employme.goal.Goal;
import com.pseudonova.employme.reward.Reward;

public interface Job
{
	OfflinePlayer getEmployer();
	Goal getGoal();
	Reward getReward();
	boolean hasFinished(Player player);
	void onComplete(Player completer);
}