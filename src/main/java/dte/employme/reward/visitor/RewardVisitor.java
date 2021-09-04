package dte.employme.reward.visitor;

import dte.employme.reward.ItemsReward;
import dte.employme.reward.MoneyReward;

public interface RewardVisitor<R>
{
	R visit(MoneyReward moneyReward);
	R visit(ItemsReward itemsReward);
}