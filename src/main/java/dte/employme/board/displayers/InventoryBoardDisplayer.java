package dte.employme.board.displayers;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.guis.JobBoardGUI;
import dte.employme.services.message.MessageService;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final MessageService messageService;
	
	public InventoryBoardDisplayer(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		new JobBoardGUI(player, jobBoard, this.messageService).show(player);
	}
}
