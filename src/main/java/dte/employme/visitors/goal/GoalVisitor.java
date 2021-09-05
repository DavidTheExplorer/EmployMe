package dte.employme.visitors.goal;

import dte.employme.goal.ItemGoal;

public interface GoalVisitor<R>
{
	R visit(ItemGoal itemGoal);
}