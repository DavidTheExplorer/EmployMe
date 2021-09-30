package dte.employme.visitors.reward;

import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;

public class RewardNameVisitor implements RewardVisitor<String>
{
	public static final RewardNameVisitor INSTANCE = new RewardNameVisitor();
	
	@Override
	public String visit(ItemsReward itemsReward)
	{
		return "Items";
	}

	@Override
	public String visit(MoneyReward moneyReward) 
	{
		return "Money";
	}
}