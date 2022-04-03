package dte.employme.services.rewards;

import org.bukkit.OfflinePlayer;

import dte.employme.rewards.Reward;

public interface JobRewardService 
{
	void refund(OfflinePlayer employer, Reward reward);
}