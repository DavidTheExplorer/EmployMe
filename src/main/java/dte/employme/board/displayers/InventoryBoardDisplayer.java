package dte.employme.board.displayers;

import java.util.Comparator;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.inventories.JobBoardGUI;
import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.job.service.JobService;
import dte.employme.messages.service.MessageService;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final Comparator<Job> orderComparator;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobIconFactory jobIconFactory;
	
	public InventoryBoardDisplayer(Comparator<Job> orderComparator, JobService jobService, MessageService messageService, JobIconFactory jobIconFactory) 
	{
		this.orderComparator = orderComparator;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobIconFactory = jobIconFactory;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		new JobBoardGUI(player, jobBoard, this.orderComparator, this.jobService, this.messageService, this.jobIconFactory).show(player);
	}
}
