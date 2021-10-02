package dte.employme.job.rewards;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.job.service.JobService;
import dte.employme.visitors.reward.RewardVisitor;

public class ItemsReward implements Reward
{
	private final ItemStack[] items;
	private final Function<Player, Inventory> itemsContainers;
	
	private static Function<Player, Inventory> globalItemsContainers;
	
	public ItemsReward(Function<Player, Inventory> itemsContainers, ItemStack... items) 
	{
		this.itemsContainers = itemsContainers;
		this.items = items;
	}
	
	public static ItemsReward of(ItemStack... items) 
	{
		ItemStack[] clonedItems = Arrays.stream(items)
				.map(ItemStack::new) //clone using the copy constructor
				.toArray(ItemStack[]::new);
		
		return new ItemsReward(globalItemsContainers, clonedItems);
	}
	
	public static void setup(JobService jobService) 
	{
		ItemsReward.globalItemsContainers = player -> jobService.getItemsContainer(player.getUniqueId());
	}
	
	@Override
	public void giveTo(Player player) 
	{
		this.itemsContainers.apply(player).addItem(this.items);
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
}