package dte.employme.board.listeners;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.inventories.InventoryFactory;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;

public class JobGoalTransferListener implements JobCompleteListener
{
	private final InventoryFactory inventoryFactory;
	
	public JobGoalTransferListener(InventoryFactory inventoryFactory) 
	{
		this.inventoryFactory = inventoryFactory;
	}
	
	@Override
	public void onJobCompleted(JobBoard jobBoard, Job job, Player whoCompleted) 
	{
		ItemStack goal = job.getGoal();
		
		InventoryUtils.remove(whoCompleted.getInventory(), goal);
		this.inventoryFactory.getItemsContainer(job.getEmployer().getUniqueId()).addItem(goal);
	}
}