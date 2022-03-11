package dte.employme.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

import dte.employme.services.playercontainer.PlayerContainerService;

public class PlayerContainerAbuseListener implements Listener
{
	private final PlayerContainerService playerContainerService;
	
	public PlayerContainerAbuseListener(PlayerContainerService playerContainerService) 
	{
		this.playerContainerService = playerContainerService;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) 
	{
		if(!this.playerContainerService.isContainer(event.getView()))
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
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void onAbuse(InventoryClickEvent event) 
	{
		InventoryView view = event.getView();
		
		if(!this.playerContainerService.isContainer(view))
			return;
		
		if(event.isShiftClick() && event.getClickedInventory() == view.getBottomInventory()) 
			event.setCancelled(true);
		
		if(event.getClickedInventory() == view.getTopInventory() && event.getCursor() != null)
		{
			switch(event.getAction())
			{
			case PLACE_ALL:
			case PLACE_ONE:
			case PLACE_SOME:
			case HOTBAR_SWAP:
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) 
	{
		if(!this.playerContainerService.isContainer(event.getView()))
			return;

		if(event.getOldCursor() == null)
			return;

		event.setCancelled(true);
	}
}
