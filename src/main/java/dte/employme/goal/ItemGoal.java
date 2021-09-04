package dte.employme.goal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.goal.visitor.GoalVisitor;

public class ItemGoal implements Goal
{
	private final ItemStack item;
	
	public ItemGoal(ItemStack item) 
	{
		this.item = new ItemStack(item);
	}
	
	public ItemStack getItem() 
	{
		return new ItemStack(this.item);
	}
	
	@Override
	public boolean hasReached(Player player) 
	{
		return player.getInventory().containsAtLeast(this.item, this.item.getAmount());
	}
	
	@Override
	public <R> R accept(GoalVisitor<R> visitor) 
	{
		return visitor.visit(this);
	}
}