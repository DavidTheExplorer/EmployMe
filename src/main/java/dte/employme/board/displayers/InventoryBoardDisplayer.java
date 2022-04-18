package dte.employme.board.displayers;

import java.util.Comparator;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.inventories.JobBoardGUI;
import dte.employme.job.Job;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final Comparator<Job> orderComparator;
	private final JobService jobService;
	private final MessageService messageService;
	
	public InventoryBoardDisplayer(Comparator<Job> orderComparator, JobService jobService, MessageService messageService) 
	{
		this.orderComparator = orderComparator;
		this.jobService = jobService;
		this.messageService = messageService;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		new JobBoardGUI(player, jobBoard, this.orderComparator, this.jobService, this.messageService).show(player);
	}
}
