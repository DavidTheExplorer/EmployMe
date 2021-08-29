package com.pseudonova.employme.reward.visitor;

import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;

public interface RewardVisitor<R>
{
	R visit(MoneyReward moneyReward);
	R visit(ItemsReward itemsReward);
}