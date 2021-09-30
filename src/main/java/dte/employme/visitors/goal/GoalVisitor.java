package dte.employme.visitors.goal;

import dte.employme.job.goals.ItemGoal;

public interface GoalVisitor<R>
{
	R visit(ItemGoal itemGoal);
}