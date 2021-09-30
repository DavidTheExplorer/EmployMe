package dte.employme.visitors.reward;

import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;

public interface RewardVisitor<R>
{
	R visit(MoneyReward moneyReward);
	R visit(ItemsReward itemsReward);
}