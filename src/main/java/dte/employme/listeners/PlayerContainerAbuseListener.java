package dte.employme.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

import dte.employme.containers.service.PlayerContainerService;

public class PlayerContainerAbuseListener implements Listener
{
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void onAbuse(InventoryClickEvent event) 
	{
		InventoryView view = event.getView();
		
		if(!PlayerContainerService.isContainer(view))
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
		if(!PlayerContainerService.isContainer(event.getView()))
			return;

		if(event.getOldCursor() == null)
			return;

		event.setCancelled(true);
	}
}
