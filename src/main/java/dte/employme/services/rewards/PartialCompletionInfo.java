package dte.employme.services.rewards;

import org.bukkit.inventory.ItemStack;

import dte.employme.rewards.PartialReward;

public class PartialCompletionInfo
{
	private final double percentage;
	private final ItemStack goal;
	private final PartialReward reward;

	public PartialCompletionInfo(double percentage, ItemStack goal, PartialReward reward)
	{
		this.percentage = percentage;
		this.goal = goal;
		this.reward = reward;
	}

	public double getPercentage() 
	{
		return this.percentage;
	}
	
	public ItemStack getGoal()
	{
		return this.goal;
	}
	
	public PartialReward getReward() 
	{
		return this.reward;
	}

	@Override
	public String toString()
	{
		return String.format("PartialCompletionInfo [percentage=%s, goal=%s, reward=%s]", this.percentage, this.goal, this.reward);
	}
}