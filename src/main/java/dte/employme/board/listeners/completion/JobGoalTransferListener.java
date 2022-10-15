package dte.employme.board.listeners.completion;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard.JobCompletionContext;
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
	public void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context) 
	{
		ItemStack goal = context.getGoal();
		
		InventoryUtils.removeIf(whoCompleted.getInventory(), job::isGoal, goal.getAmount());
		this.playerContainerService.getItemsContainer(job.getEmployer().getUniqueId()).addItem(goal);
	}
}