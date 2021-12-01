package dte.employme.visitors.reward;

import org.bukkit.entity.Player;

import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.utils.InventoryUtils;
import net.milkbowl.vault.economy.Economy;

public class RewardTaker implements RewardVisitor<Void>
{
	private final Player employer;
	private final Economy economy;

	public RewardTaker(Player employer, Economy economy) 
	{
		this.employer = employer;
		this.economy = economy;
	}

	@Override
	public Void visit(MoneyReward moneyReward) 
	{
		this.economy.withdrawPlayer(this.employer, moneyReward.getPayment());
		return null;
	}

	@Override
	public Void visit(ItemsReward itemsReward) 
	{
		itemsReward.getItems().forEach(reward -> InventoryUtils.remove(this.employer.getInventory(), reward));
		return null;
	}
}