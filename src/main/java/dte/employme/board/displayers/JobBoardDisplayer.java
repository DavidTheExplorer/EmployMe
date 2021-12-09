package dte.employme.board.displayers;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;

@FunctionalInterface
public interface JobBoardDisplayer
{
	void display(Player player, JobBoard jobBoard);
}