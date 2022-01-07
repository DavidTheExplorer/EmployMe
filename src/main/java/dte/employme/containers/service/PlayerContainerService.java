package dte.employme.containers.service;

import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface PlayerContainerService
{
	Inventory getItemsContainer(UUID playerUUID);
	Inventory getRewardsContainer(UUID playerUUID);
	
	void loadContainers();
	void saveContainers();
	
	String TITLE_PATTERN = "Claim your %s:";
	
	public static boolean isContainer(InventoryView view) 
	{
		return view.getTitle().matches(TITLE_PATTERN.replace("%s", "[a-zA-Z\\d]+"));
	}
}
