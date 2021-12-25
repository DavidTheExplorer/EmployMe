package dte.employme.board.displayers;

import java.util.Comparator;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.inventories.JobBoardGUI;
import dte.employme.job.Job;
import dte.employme.job.service.JobService;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final Comparator<Job> orderComparator;
	private final JobService jobService;
	
	public InventoryBoardDisplayer(Comparator<Job> orderComparator, JobService jobService) 
	{
		this.orderComparator = orderComparator;
		this.jobService = jobService;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		new JobBoardGUI(player, jobBoard, this.orderComparator, this.jobService).show(player);
	}
}
