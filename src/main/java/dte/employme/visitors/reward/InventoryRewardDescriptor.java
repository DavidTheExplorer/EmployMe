package dte.employme.visitors.reward;

import static dte.employme.utils.ChatColorUtils.colorize;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import dte.employme.reward.ItemsReward;
import dte.employme.reward.MoneyReward;
import dte.employme.utils.java.EnumUtils;

public class InventoryRewardDescriptor implements RewardVisitor<List<String>>
{
	public static final InventoryRewardDescriptor INSTANCE = new InventoryRewardDescriptor();
	
	private static final List<String> BASE = Lists.newArrayList(colorize(String.format("&6&n&lPayment&6:")));
	
	@Override
	public List<String> visit(MoneyReward moneyReward)
	{
		List<String> lore = new ArrayList<>(BASE);

		//add the payment's amount
		lore.set(0, lore.get(0) + colorize(String.format(" &f%.2f$", moneyReward.getPayment())));

		return lore;
	}

	@Override
	public List<String> visit(ItemsReward itemsReward)
	{
		List<String> lore = new ArrayList<>(BASE);
		
		//add the items' descriptions
		itemsReward.getItems().stream()
		.map(item -> colorize(String.format("&f&o%s - &6%d", EnumUtils.fixEnumName(item.getType()), item.getAmount())))
		.forEach(lore::add);
		
		return lore;
	}
}
