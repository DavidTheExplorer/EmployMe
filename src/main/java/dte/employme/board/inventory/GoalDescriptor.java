package dte.employme.board.inventory;

import static dte.employme.utils.ChatColorUtils.colorize;

import org.bukkit.inventory.ItemStack;

import dte.employme.goal.ItemGoal;
import dte.employme.goal.visitor.GoalVisitor;
import dte.employme.utils.EnumUtils;

public class GoalDescriptor implements GoalVisitor<String>
{
	public static final GoalDescriptor INSTANCE = new GoalDescriptor();
	
	@Override
	public String visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		return colorize(String.format("&b%d %s&f.", item.getAmount(), EnumUtils.fixEnumName(item.getType())));
	}
}