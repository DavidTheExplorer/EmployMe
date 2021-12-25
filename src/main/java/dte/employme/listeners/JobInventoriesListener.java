package dte.employme.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.board.displayers.InventoryBoardDisplayer;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.inventories.ItemsRewardPreviewGUI;
import dte.employme.items.JobItemUtils;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.service.JobService;

public class JobInventoriesListener implements Listener
{
	private final JobBoard globalJobBoard;
	private final JobService jobService;
	
	public JobInventoriesListener(JobBoard globalJobBoard, JobService jobService) 
	{
		this.globalJobBoard = globalJobBoard;
		this.jobService = jobService;
	}
	
	@EventHandler
	public void onJobComplete(InventoryClickEvent event) 
	{
		//TODO: replace with guard clauses
		InventoryBoardDisplayer.getRepresentedBoard(event.getInventory()).ifPresent(inventoryBoard -> 
		{
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			
			if(item == null)
				return;
			
			Player player = (Player) event.getWhoClicked();
			
			JobItemUtils.getJobID(item)
			.flatMap(inventoryBoard::getJobByID)
			.ifPresent(job ->
			{
				//Right click = preview mode for jobs that offer items
				if(event.isRightClick() && job.getReward() instanceof ItemsReward)
				{
					ItemsRewardPreviewGUI gui = new ItemsRewardPreviewGUI((ItemsReward) job.getReward());
					gui.setOnClose(closeEvent -> player.openInventory(event.getInventory()));
					gui.show(player);
				}
				
				//the user wants to finish the job
				else if(this.jobService.hasFinished(player, job))
				{
					player.closeInventory();
					this.globalJobBoard.completeJob(job, player);
				}
			});
		});
	}
	
	@EventHandler
	public void onContainersClick(InventoryClickEvent event) 
	{
		if(!PlayerContainerService.isContainer(event.getView()))
			return;
		
		switch(event.getRawSlot()) 
		{
		case 43:
		case 44:
		case 52:
		case 53:
			event.setCancelled(true);
		}
	}
}