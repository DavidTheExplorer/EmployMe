package com.pseudonova.employme.conversations;

import org.bukkit.entity.Player;

import com.pseudonova.employme.EmployMe;
import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;
import com.pseudonova.employme.reward.visitor.RewardVisitor;
import com.pseudonova.employme.utils.InventoryUtils;

public class RewardTaker implements RewardVisitor<Void>
{
	private final Player employer;

	public RewardTaker(Player employer) 
	{
		this.employer = employer;
	}

	@Override
	public Void visit(MoneyReward moneyReward) 
	{
		EmployMe.getInstance().getEconomy().withdrawPlayer(this.employer, moneyReward.getPayment());
		return null;
	}

	@Override
	public Void visit(ItemsReward itemsReward) 
	{
		itemsReward.getItems().forEach(reward -> InventoryUtils.remove(this.employer.getInventory(), reward));
		return null;
	}
}