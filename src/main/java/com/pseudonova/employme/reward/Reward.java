package com.pseudonova.employme.reward;

import org.bukkit.entity.Player;

import com.pseudonova.employme.reward.visitor.RewardVisitor;

public interface Reward
{
	void giveTo(Player whoCompleted);
	
	<R> R accept(RewardVisitor<R> visitor);
}