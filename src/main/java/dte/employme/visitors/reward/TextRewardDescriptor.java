package dte.employme.visitors.reward;

import static java.util.stream.Collectors.joining;

import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.utils.java.EnumUtils;

public class TextRewardDescriptor implements RewardVisitor<String>
{
	public static final TextRewardDescriptor INSTANCE = new TextRewardDescriptor();
	
	@Override
	public String visit(MoneyReward moneyReward) 
	{
		return String.format("%.2f$", moneyReward.getPayment());
	}

	@Override
	public String visit(ItemsReward itemsReward) 
	{
		return itemsReward.getItems().stream()
				.map(item -> String.format("%d %s", item.getAmount(), EnumUtils.fixEnumName(item.getType())))
				.collect(joining(", "));
	}
}