package com.pseudonova.employme.goal.visitor;

import com.pseudonova.employme.goal.ItemGoal;

public interface GoalVisitor<R>
{
	R visit(ItemGoal itemGoal);
}