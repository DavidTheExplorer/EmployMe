package com.pseudonova.employme.reward.visitor;

import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;

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