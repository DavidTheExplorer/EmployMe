package com.pseudonova.employme.conversations;

import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pseudonova.employme.EmployMe;
import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;
import com.pseudonova.employme.reward.visitor.RewardVisitor;
import com.pseudonova.employme.utils.InventoryUtils;

public class RewardTaker implements RewardVisitor<Void>
{
	private final Supplier<Player> employer;

	public RewardTaker(Supplier<Player> employer) 
	{
		this.employer = employer;
	}

	@Override
	public Void visit(MoneyReward moneyReward) 
	{
		EmployMe.getInstance().getEconomy().withdrawPlayer(this.employer.get(), moneyReward.getPayment());
		return null;
	}

	@Override
	public Void visit(ItemsReward itemsReward) 
	{
		for(ItemStack reward : itemsReward.getItems()) 
			InventoryUtils.remove(this.employer.get().getInventory(), reward);

		return null;
	}
}