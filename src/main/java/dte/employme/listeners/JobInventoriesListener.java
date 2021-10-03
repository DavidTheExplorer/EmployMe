package dte.employme.listeners;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.InventoryJobBoard;
import dte.employme.board.JobBoard;
import dte.employme.items.ItemFactory;
import dte.employme.job.service.JobService;
import dte.employme.messages.Message;
import dte.employme.utils.InventoryUtils;

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
			this.jobService.buildMoneyJobConversation(employer).begin();
			break;
			
		case CHEST:
			employer.openInventory(Bukkit.createInventory(null, 9 * 6, "What would you like to offer?"));
			break;
		}
	}
	
	@EventHandler
	public void onItemsJobOfferingInventory(InventoryCloseEvent event) 
	{
		if(!event.getView().getTitle().equals("What would you like to offer?"))
			return;
		
		Player player = (Player) event.getPlayer();
		List<ItemStack> offeredItems = InventoryUtils.itemsStream(event.getInventory(), true).collect(toList());
		
		if(offeredItems.isEmpty()) 
		{
			Message.sendGeneralMessage(player, Message.ITEMS_JOB_NO_ITEMS_WARNING);
			return;
		}
		
		this.jobService.buildItemsJobConversation(player, offeredItems).begin();
	}
	
	@EventHandler
	public void onContainersClick(InventoryClickEvent event) 
	{
		if(!event.getView().getTitle().matches("Claim your [a-zA-Z]+:"))
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
