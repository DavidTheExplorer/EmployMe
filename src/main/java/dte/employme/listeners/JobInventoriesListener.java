package dte.employme.listeners;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.InventoryJobBoard;
import dte.employme.board.JobBoard;
import dte.employme.items.ItemFactory;
import dte.employme.job.service.JobService;
import dte.employme.messages.Message;

public class JobInventoriesListener implements Listener
{
	private final JobService jobService;
	
	private final JobBoard globalJobBoard;
	
	public JobInventoriesListener(JobService jobService, JobBoard globalJobBoard) 
	{
		this.jobService = jobService;
		this.globalJobBoard = globalJobBoard;
	}
	
	@EventHandler
	public void onDeletionMark(InventoryClickEvent event) 
	{
		if(!event.getView().getTitle().equals("Select Jobs to Delete"))
			return;
		
		event.setCancelled(true);
		
		ItemStack item = event.getCurrentItem();
		
		if(item == null)
			return;
		
		Player employer = (Player) event.getWhoClicked();
		
		ItemFactory.getJobID(item)
		.flatMap(this.globalJobBoard::getJobByID)
		.ifPresent(job -> 
		{
			employer.closeInventory();
			this.globalJobBoard.removeJob(job);
			job.getReward().giveTo(employer);
			
			Message.JOB_SUCCESSFULLY_DELETED.sendTo(employer);
		});
	}
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void onTypeSelect(InventoryClickEvent event) 
	{
		if(!event.getView().getTitle().equals("Create a new Job"))
			return;
		
		event.setCancelled(true);
		
		ItemStack item = event.getCurrentItem();
		
		if(item == null)
			return;
		
		Player employer = (Player) event.getWhoClicked();
		
		employer.closeInventory();
		
		switch(event.getCurrentItem().getType())
		{
		case GOLD_INGOT:
			this.jobService.buildMoneyJobConversation(employer).ifPresent(Conversation::begin);
			break;
			
		case CHEST:
			this.jobService.buildItemsJobConversation(employer).ifPresent(Conversation::begin);
			break;
		}
	}
	
	@EventHandler
	public void onJobComplete(InventoryClickEvent event) 
	{
		Inventory inventory = event.getInventory();

		InventoryJobBoard.getRepresentedBoard(inventory).ifPresent(inventoryBoard -> 
		{
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			
			if(item == null)
				return;
			
			Player player = (Player) event.getWhoClicked();
			
			ItemFactory.getJobID(item)
			.flatMap(inventoryBoard::getJobByID)
			.filter(job -> job.getGoal().hasReached(player))
			.ifPresent(job ->
			{
				player.closeInventory();
				this.jobService.onComplete(job, player);
			});
		});
	}
}
