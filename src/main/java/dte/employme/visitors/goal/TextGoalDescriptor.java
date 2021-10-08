package dte.employme.visitors.goal;

import org.bukkit.inventory.ItemStack;

import dte.employme.job.goals.ItemGoal;
import dte.employme.utils.ItemStackUtils;

public class TextGoalDescriptor implements GoalVisitor<String>
{
	public static final TextGoalDescriptor INSTANCE = new TextGoalDescriptor();
	
	@Override
	public String visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		return "Get " + ItemStackUtils.describe(item);
	}
}
