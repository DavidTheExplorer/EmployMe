package dte.employme.reward;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.visitors.reward.RewardVisitor;

public class ItemsReward implements Reward
{
	private final ItemStack[] items;
	
	public ItemsReward(ItemStack... items) 
	{
		this.items = Arrays.stream(items)
				.map(ItemStack::new) //clone using the copy constructor
				.toArray(ItemStack[]::new);
	}
	
	@Override
	public void giveTo(Player player) 
	{
		Collection<ItemStack> remainingItems = player.getInventory().addItem(this.items).values();
		
		remainingItems.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
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