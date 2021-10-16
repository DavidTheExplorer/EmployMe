package dte.employme.board.listeners;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;

public class JobGoalTransferListener implements JobCompleteListener
{
	private final PlayerContainerService playerContainerService;
	
	public JobGoalTransferListener(PlayerContainerService playerContainerService) 
	{
		this.playerContainerService = playerContainerService;
	}
	
	@Override
	public void onJobCompleted(JobBoard jobBoard, Job job, Player whoCompleted) 
	{
		ItemStack goal = job.getGoal();
		
		InventoryUtils.remove(whoCompleted.getInventory(), goal);
		this.playerContainerService.getItemsContainer(job.getEmployer().getUniqueId()).addItem(goal);
	}
}