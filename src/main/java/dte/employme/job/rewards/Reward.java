package dte.employme.job.rewards;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import dte.employme.visitors.reward.RewardVisitor;

public interface Reward extends ConfigurationSerializable
{
	void giveTo(OfflinePlayer offlinePlayer);
	
	<R> R accept(RewardVisitor<R> visitor);
}