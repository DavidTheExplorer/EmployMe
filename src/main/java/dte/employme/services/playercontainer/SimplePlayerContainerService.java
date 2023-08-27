package dte.employme.services.playercontainer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;

import dte.employme.guis.containers.PlayerContainerGUIFactory;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;
import dte.spigotconfiguration.SpigotConfig;

public class SimplePlayerContainerService implements PlayerContainerService
{
	private final Map<UUID, ItemPaletteGUI>
	itemsContainers = new HashMap<>(),
	rewardsContainers = new HashMap<>();
	
	private final SpigotConfig itemsContainersConfig, rewardsContainersConfig;
	private final PlayerContainerGUIFactory playerContainerGUIFactory;

	public SimplePlayerContainerService(SpigotConfig itemsContainersConfig, SpigotConfig rewardsContainersConfig, PlayerContainerGUIFactory playerContainerGUIFactory) 
	{
		this.itemsContainersConfig = itemsContainersConfig;
		this.rewardsContainersConfig = rewardsContainersConfig;
		this.playerContainerGUIFactory = playerContainerGUIFactory;
	}
	
	@Override
	public ItemPaletteGUI getItemsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> this.playerContainerGUIFactory.create("Items"));
	}
	
	@Override
	public ItemPaletteGUI getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> this.playerContainerGUIFactory.create("Rewards"));
	}
	
	@Override
	public void loadContainers() 
	{
		this.itemsContainers.putAll(loadContainers(this.itemsContainersConfig, "Items"));
		this.rewardsContainers.putAll(loadContainers(this.rewardsContainersConfig, "Rewards"));
	}
	
	@Override
	public void saveContainers() 
	{
		saveContainers(this.itemsContainersConfig, this.itemsContainers);
		saveContainers(this.rewardsContainersConfig, this.rewardsContainers);
	}
	
	private Map<UUID, ItemPaletteGUI> loadContainers(SpigotConfig containersConfig, String subject) 
	{
		return containersConfig.getKeys(false).stream()
				.map(UUID::fromString)
				.collect(toMap(Function.identity(), playerUUID -> 
				{
					List<ItemStack> playerItems = containersConfig.getSection(playerUUID.toString()).getKeys(false).stream()
							.map(slot -> containersConfig.parseItem(playerUUID + "." + slot))
							.collect(toList());

					ItemPaletteGUI container = this.playerContainerGUIFactory.create(subject);
					playerItems.forEach(container::addItem);

					return container;
				}));
	}
	
	private static void saveContainers(SpigotConfig containersConfig, Map<UUID, ItemPaletteGUI> containers) 
	{
		try
		{
			containersConfig.clear();
			
			containers.forEach((playerUUID, container) -> 
			{
				List<ItemStack> items = container.getStoredItems();
				
				for(int i = 0; i < items.size(); i++) 
					containersConfig.set(playerUUID.toString() + "." + i, items.get(i)); 
			});
			
			containersConfig.save();
			
		} 
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
	}
}