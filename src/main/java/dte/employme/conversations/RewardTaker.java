package dte.employme.conversations;

import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.reward.ItemsReward;
import dte.employme.reward.MoneyReward;
import dte.employme.reward.visitor.RewardVisitor;
import dte.employme.utils.InventoryUtils;

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