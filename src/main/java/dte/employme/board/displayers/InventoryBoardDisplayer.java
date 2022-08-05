package dte.employme.board.displayers;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.guis.JobBoardGUI;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final JobService jobService;
	private final MessageService messageService;
	
	public InventoryBoardDisplayer(JobService jobService, MessageService messageService) 
	{
		this.jobService = jobService;
		this.messageService = messageService;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		new JobBoardGUI(player, jobBoard, this.jobService, this.messageService).show(player);
	}
}
