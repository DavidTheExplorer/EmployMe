package dte.employme.goal;

import org.bukkit.entity.Player;

public interface FunctionalGoal extends Goal
{
	void onReach(Player completer);
}