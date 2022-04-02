package dte.employme.board.listenable;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.board.listenable.ListenableJobBoard.JobCompleteListener;
import dte.employme.job.Job;
import dte.employme.services.job.JobService;
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
		
		InventoryUtils.removeIf(whoCompleted.getInventory(), item -> JobService.isGoal(item, goal), goal.getAmount());
		this.playerContainerService.getItemsContainer(employerUUID).addItem(goal);
	}
}