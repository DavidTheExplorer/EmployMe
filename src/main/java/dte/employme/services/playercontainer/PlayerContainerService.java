package dte.employme.services.playercontainer;

import java.util.UUID;

import dte.employme.guis.playercontainer.PlayerContainerGUI;

public interface PlayerContainerService
{
	PlayerContainerGUI getItemsContainer(UUID playerUUID);
	
	void loadContainers();
	void saveContainers();
}