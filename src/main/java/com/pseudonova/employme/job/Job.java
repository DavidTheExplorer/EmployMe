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
	
	default void onComplete(Player completer) 
	{
		getReward().giveTo(completer);
	}
	
	default boolean hasFinished(Player player) 
	{
		return getGoal().hasReached(player);
	}
}