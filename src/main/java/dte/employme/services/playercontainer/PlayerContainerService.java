package dte.employme.services.playercontainer;

import java.util.UUID;

import dte.employme.inventories.PlayerContainerGUI;

public interface PlayerContainerService
{
	PlayerContainerGUI getItemsContainer(UUID playerUUID);
	PlayerContainerGUI getRewardsContainer(UUID playerUUID);
	
	void loadContainers();
	void saveContainers();
}