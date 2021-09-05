package dte.employme.listeners;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.service.JobService;

public class JobCreationInventoriesListener implements Listener
{
	private final JobService jobService;
	
	public JobCreationInventoriesListener(JobService jobService) 
	{
		this.jobService = jobService;
	}
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void onTypeSelection(InventoryClickEvent event) 
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
}
