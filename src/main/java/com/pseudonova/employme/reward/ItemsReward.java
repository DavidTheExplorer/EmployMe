package com.pseudonova.employme.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pseudonova.employme.reward.visitor.RewardVisitor;
import com.pseudonova.employme.utils.IterableUtils;

public class ItemsReward implements Reward
{
	private final ItemStack[] items;
	
	private ItemsReward(ItemStack[] items) 
	{
		this.items = items;
	}
	
	public static ItemsReward of(Iterable<ItemStack> iterable) 
	{
		if(!iterable.iterator().hasNext())
			throw new IllegalArgumentException("Can't create an Item Reward of no items!");
		
		ItemStack[] itemsArray = IterableUtils.stream(iterable)
				.map(ItemStack::new)
				.toArray(ItemStack[]::new);
		
		return new ItemsReward(itemsArray);
	}
	
	@Override
	public void giveTo(Player whoCompleted) 
	{
		whoCompleted.getInventory().addItem(this.items);
	}
	
	public ItemStack[] getItems() 
	{
		return this.items.clone();
	}

	@Override
	public <R> R accept(RewardVisitor<R> visitor) 
	{
		return visitor.visit(this);
	}
}
