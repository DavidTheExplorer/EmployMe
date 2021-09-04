package dte.employme.reward.visitor;

import dte.employme.reward.ItemsReward;
import dte.employme.reward.MoneyReward;

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