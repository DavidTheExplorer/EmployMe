package dte.employme.visitors.goal;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.inventory.ItemStack;

import dte.employme.job.goals.ItemGoal;
import dte.employme.utils.ItemStackUtils;

public class InventoryGoalDescriptor implements GoalVisitor<String>
{
	public static final InventoryGoalDescriptor INSTANCE = new InventoryGoalDescriptor();
	
	@Override
	public String visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		return AQUA + ItemStackUtils.describe(item) + WHITE + ".";
	}
}