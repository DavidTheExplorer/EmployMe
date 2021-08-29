package com.pseudonova.employme.reward.visitor;

import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;

public class RewardTypeVisitor implements RewardVisitor<String>
{
	public static final RewardTypeVisitor INSTANCE = new RewardTypeVisitor();
	
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