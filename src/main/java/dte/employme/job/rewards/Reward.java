package dte.employme.job.rewards;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import dte.employme.visitors.reward.RewardVisitor;

public interface Reward extends ConfigurationSerializable
{
	void giveTo(Player player);
	
	<R> R accept(RewardVisitor<R> visitor);
}