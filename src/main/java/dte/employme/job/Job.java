package dte.employme.job;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.rewards.Reward;

public interface Job extends ConfigurationSerializable
{
	OfflinePlayer getEmployer();
	ItemStack getGoal();
	Reward getReward();
}