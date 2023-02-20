package dte.employme.services.playercontainer;

import static dte.employme.messages.MessageKey.CONTAINER_CLAIM_INSTRUCTION;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;

import dte.employme.guis.playercontainer.PlayerContainerGUI;
import dte.employme.services.message.MessageService;
import dte.spigotconfiguration.SpigotConfig;

public class SimplePlayerContainerService implements PlayerContainerService
{
	private final Map<UUID, PlayerContainerGUI>
	itemsContainers = new HashMap<>(),
	rewardsContainers = new HashMap<>();

	private final SpigotConfig itemsContainersConfig, rewardsContainersConfig;
	private final MessageService messageService;

	public SimplePlayerContainerService(SpigotConfig itemsContainersConfig, SpigotConfig rewardsContainersConfig, MessageService messageService) 
	{
		this.itemsContainersConfig = itemsContainersConfig;
		this.rewardsContainersConfig = rewardsContainersConfig;
		this.messageService = messageService;
	}
	
	@Override
	public PlayerContainerGUI getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> new PlayerContainerGUI(createContainerTitle("Items"), this.messageService));
	}
	
	@Override
	public PlayerContainerGUI getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> new PlayerContainerGUI(createContainerTitle("Rewards"), this.messageService));
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
	
	private String createContainerTitle(String subject) 
	{
		return this.messageService.getMessage(CONTAINER_CLAIM_INSTRUCTION)
				.inject("container subject", subject)
				.first();
	}
	
	private Map<UUID, PlayerContainerGUI> loadContainers(SpigotConfig containersConfig, String subject) 
	{
		return containersConfig.getKeys(false).stream()
				.map(UUID::fromString)
				.collect(toMap(Function.identity(), playerUUID -> 
				{
					List<ItemStack> playerItems = containersConfig.getSection(playerUUID.toString()).getKeys(false).stream()
							.map(slot -> containersConfig.getItemStack(playerUUID + "." + slot))
							.collect(toList());

					PlayerContainerGUI container = new PlayerContainerGUI(subject, this.messageService);
					playerItems.forEach(container::addItem);

					return container;
				}));
	}
	
	private static void saveContainers(SpigotConfig containersConfig, Map<UUID, PlayerContainerGUI> containers) 
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