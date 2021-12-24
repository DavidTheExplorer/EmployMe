package dte.employme.job;

import static java.util.Comparator.comparing;

import java.util.Comparator;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.rewards.Reward;

public interface Job extends ConfigurationSerializable
{
	OfflinePlayer getEmployer();
	ItemStack getGoal();
	Reward getReward();
	
	
	
	Comparator<Job>
	ORDER_BY_EMPLOYER_NAME = comparing(job -> job.getEmployer().getName().toLowerCase()),
	ORDER_BY_GOAL_NAME = comparing(job -> job.getGoal().getType().name());
}