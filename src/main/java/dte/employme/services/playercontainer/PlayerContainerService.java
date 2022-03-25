package dte.employme.services.playercontainer;

import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface PlayerContainerService
{
	Inventory getItemsContainer(UUID playerUUID);
	Inventory getRewardsContainer(UUID playerUUID);
	boolean isContainer(InventoryView view);
	
	void loadContainers();
	void saveContainers();
}