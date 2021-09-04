package dte.employme.reward;

import java.util.List;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.reward.visitor.RewardVisitor;

public class ItemsReward implements Reward
{
	private final ItemStack[] items;
	
	public ItemsReward(ItemStack... items) 
	{
		this.items = Validate.notEmpty(items, "Can't create an Item Reward of no items!").clone();
	}
	
	@Override
	public void giveTo(Player whoCompleted) 
	{
		whoCompleted.getInventory().addItem(this.items);
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
