package com.pseudonova.employme.board.inventory;

import static com.pseudonova.employme.utils.ChatColorUtils.colorize;

import org.bukkit.inventory.ItemStack;

import com.pseudonova.employme.goal.ItemGoal;
import com.pseudonova.employme.goal.visitor.GoalVisitor;
import com.pseudonova.employme.utils.EnumUtils;

public class GoalDescriptor implements GoalVisitor<String>
{
	public static final GoalDescriptor INSTANCE = new GoalDescriptor();
	
	@Override
	public String visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		return colorize(String.format("&b%d %s&f.", item.getAmount(), EnumUtils.fixEnumName(item.getType())));
		
		//return AQUA.toString() + item.getAmount() + " " + EnumUtils.fixEnumName(item.getType()) + WHITE + ".";
	}
}