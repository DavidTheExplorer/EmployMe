package dte.employme.services.playercontainer;

import java.util.UUID;

import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;

public interface PlayerContainerService
{
	ItemPaletteGUI getItemsContainer(UUID playerUUID);
	ItemPaletteGUI getRewardsContainer(UUID playerUUID);
	
	void loadContainers();
	void saveContainers();
}