package dte.employme.board.listeners.completion;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.services.playercontainer.PlayerContainerService;
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
		UUID employerUUID = job.getEmployer().getUniqueId();
		
		InventoryUtils.removeIf(whoCompleted.getInventory(), job::isGoal, goal.getAmount());
		this.playerContainerService.getItemsContainer(employerUUID).addItem(goal);
	}
}