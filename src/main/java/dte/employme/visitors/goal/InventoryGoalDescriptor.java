package dte.employme.visitors.goal;

import static dte.employme.utils.ChatColorUtils.colorize;

import org.bukkit.inventory.ItemStack;

import dte.employme.goal.ItemGoal;
import dte.employme.utils.EnumUtils;

public class InventoryGoalDescriptor implements GoalVisitor<String>
{
	public static final InventoryGoalDescriptor INSTANCE = new InventoryGoalDescriptor();
	
	@Override
	public String visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		return colorize(String.format("&b%d %s&f.", item.getAmount(), EnumUtils.fixEnumName(item.getType())));
	}
}