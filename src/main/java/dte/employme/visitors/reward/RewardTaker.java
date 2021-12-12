package dte.employme.visitors.reward;

import org.bukkit.entity.Player;

import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.utils.InventoryUtils;
import net.milkbowl.vault.economy.Economy;

public class RewardTaker implements RewardVisitor<Void>
{
	private final Player player;
	private final Economy economy;

	public RewardTaker(Player player, Economy economy) 
	{
		this.player = player;
		this.economy = economy;
	}

	@Override
	public Void visit(MoneyReward moneyReward) 
	{
		this.economy.withdrawPlayer(this.player, moneyReward.getPayment());
		return null;
	}

	@Override
	public Void visit(ItemsReward itemsReward) 
	{
		itemsReward.getItems().forEach(reward -> InventoryUtils.remove(this.player.getInventory(), reward));
		return null;
	}
}