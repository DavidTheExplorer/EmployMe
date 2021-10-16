package dte.employme.containers.service;

import java.util.UUID;

import dte.employme.containers.PlayerContainer;

public interface PlayerContainerService
{
	PlayerContainer getItemsContainer(UUID playerUUID);
	PlayerContainer getRewardsContainer(UUID playerUUID);
	
	void loadContainers();
	void saveContainers();
}
