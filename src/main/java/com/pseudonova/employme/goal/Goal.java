package com.pseudonova.employme.goal;

import org.bukkit.entity.Player;

import com.pseudonova.employme.goal.visitor.GoalVisitor;

public interface Goal
{
	boolean hasReached(Player player);
	
	<R> R accept(GoalVisitor<R> visitor);
}