package dte.employme.job.rewards;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.job.service.JobService;
import dte.employme.visitors.reward.RewardVisitor;

public class ItemsReward implements Reward
{
	private final Iterable<ItemStack> items;
	
	private static JobService jobService;
	
	public ItemsReward(Iterable<ItemStack> items) 
	{
		this.items = items;
	}
	
	@Override
	public void giveTo(Player player)
	{
		Inventory playerContainer = jobService.getRewardsContainer(player.getUniqueId());
		
		this.items.forEach(playerContainer::addItem);
	}
	
	public List<ItemStack> getItems() 
	{
		return Lists.newArrayList(this.items);
	}

	@Override
	public <R> R accept(RewardVisitor<R> visitor) 
	{
		return visitor.visit(this);
	}
	
	public static void setup(JobService jobService) 
	{
		ItemsReward.jobService = jobService;
	}
}