package dte.employme.services.job.reward;

import org.bukkit.OfflinePlayer;

import dte.employme.job.rewards.Reward;

public interface RewardService 
{
	void refund(OfflinePlayer employer, Reward reward);
}