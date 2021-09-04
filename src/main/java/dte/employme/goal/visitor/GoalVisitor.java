package dte.employme.goal.visitor;

import dte.employme.goal.ItemGoal;

public interface GoalVisitor<R>
{
	R visit(ItemGoal itemGoal);
}