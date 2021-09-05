package dte.employme.reward;

import org.bukkit.entity.Player;

import dte.employme.reward.visitor.RewardVisitor;

public interface Reward
{
	void giveTo(Player whoCompleted);
	
	<R> R accept(RewardVisitor<R> visitor);
}