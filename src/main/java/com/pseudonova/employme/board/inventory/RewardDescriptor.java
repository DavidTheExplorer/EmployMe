package com.pseudonova.employme.board.inventory;

import static com.pseudonova.employme.utils.ChatColorUtils.colorize;

import java.util.Arrays;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;

import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;
import com.pseudonova.employme.reward.visitor.RewardVisitor;
import com.pseudonova.employme.utils.EnumUtils;

public class RewardDescriptor implements RewardVisitor<String[]>
{
	public static final RewardDescriptor INSTANCE = new RewardDescriptor();
	
	private static final String[] BASE = {colorize(String.format("&6&n&lPayment&6:"))};
	
	@Override
	public String[] visit(MoneyReward moneyReward)
	{
		String[] base = BASE.clone();
		
		//add the payment amount
		base[0] += colorize(String.format(" &f%.2f$", moneyReward.getPayment()));
		
		return base;
	}
	
	@Override
	public String[] visit(ItemsReward itemsReward)
	{
		String[] itemsDescription = Arrays.stream(itemsReward.getItems())
				.map(item -> colorize(String.format("&f&o%s - &6%d", EnumUtils.fixEnumName(item.getType()), item.getAmount())))
				.toArray(String[]::new);
		
		return ArrayUtils.addAll(BASE, itemsDescription);
	}
}
