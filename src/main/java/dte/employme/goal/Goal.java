package dte.employme.goal;

import org.bukkit.entity.Player;

import dte.employme.visitors.goal.GoalVisitor;

public interface Goal
{
	boolean hasReached(Player player);
	
	<R> R accept(GoalVisitor<R> visitor);
}