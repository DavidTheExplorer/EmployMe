package dte.employme.containers.service;

import static dte.employme.messages.MessageKey.CONTAINER_CLAIM_INSTRUCTION;
import static dte.employme.messages.MessageKey.ITEMS;
import static dte.employme.messages.MessageKey.ITEMS_CONTAINER_DESCRIPTION;
import static dte.employme.messages.MessageKey.REWARDS;
import static dte.employme.messages.MessageKey.REWARDS_CONTAINER_DESCRIPTION;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import dte.employme.config.ConfigFile;
import dte.employme.containers.PlayerContainerBuilder;
import dte.employme.messages.service.MessageService;

public class SimplePlayerContainerService implements PlayerContainerService
{
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();
	private final ConfigFile itemsContainersConfig, rewardsContainersConfig;
	private final MessageService messageService;
	
	private static final Set<Integer> INVALID_SLOTS = Sets.newHashSet(43, 44, 52, 53);
	
	public SimplePlayerContainerService(ConfigFile itemsContainersConfig, ConfigFile rewardsContainersConfig, MessageService messageService) 
	{
		this.itemsContainersConfig = itemsContainersConfig;
		this.rewardsContainersConfig = rewardsContainersConfig;
		this.messageService = messageService;
	}
	
	@Override
	public Inventory getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> createItemsContainer());
	}

	@Override
	public Inventory getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> createRewardsContainer());
	}
	
	@Override
	public boolean isContainer(InventoryView view) 
	{
		return view.getTitle().matches(this.messageService.getMessage(CONTAINER_CLAIM_INSTRUCTION)
				.inject("%container subject%", "[a-zA-Z\\d]+")
				.first());
	}

	@Override
	public void loadContainers() 
	{
		loadContainers(this.itemsContainersConfig, this.itemsContainers, this::createItemsContainer);
		loadContainers(this.rewardsContainersConfig, this.rewardsContainers, this::createRewardsContainer);
	}

	@Override
	public void saveContainers() 
	{
		saveContainers(this.itemsContainers, this.itemsContainersConfig);
		saveContainers(this.rewardsContainers, this.rewardsContainersConfig);
	}
	
	private Inventory createItemsContainer() 
	{
		return new PlayerContainerBuilder()
				.withMessageService(this.messageService)
				.of(this.messageService.getMessage(ITEMS).first())
				.withHelp(this.messageService.getMessage(ITEMS_CONTAINER_DESCRIPTION).toArray())
				.build();
	}
	
	private Inventory createRewardsContainer() 
	{
		return new PlayerContainerBuilder()
				.withMessageService(this.messageService)
				.of(this.messageService.getMessage(REWARDS).first())
				.withHelp(this.messageService.getMessage(REWARDS_CONTAINER_DESCRIPTION).toArray())
				.build();
	}

	private static void loadContainers(ConfigFile containersConfig, Map<UUID, Inventory> containersMap, Supplier<Inventory> containerCreator) 
	{
		containersConfig.getConfig().getKeys(false).stream()
		.map(UUID::fromString)
		.forEach(playerUUID -> 
		{
			Map<Integer, ItemStack> playerItems = containersConfig.getConfig().getConfigurationSection(playerUUID.toString())
					.getKeys(false).stream()
					.map(Integer::parseInt)
					.collect(toMap(Function.identity(), index -> containersConfig.getConfig().getItemStack(playerUUID + "." + index)));
			
			
			Inventory container = containerCreator.get();
			playerItems.forEach(container::setItem);

			containersMap.put(playerUUID, container);
		});
	}

	private static void saveContainers(Map<UUID, Inventory> containersMap, ConfigFile containersConfig) 
	{
		Map<UUID, Map<Integer, ItemStack>> playersItems = containersMap.entrySet().stream()
				.collect(toMap(Entry::getKey, entry -> 
				{
					Inventory container = entry.getValue();
					
					return IntStream.range(0, container.getSize())
							.filter(i -> !INVALID_SLOTS.contains(i))
							.filter(i -> container.getItem(i) != null)
							.boxed()
							.collect(toMap(Function.identity(), container::getItem));
				}));

		playersItems.forEach((playerUUID, playerItems) -> containersConfig.getConfig().set(playerUUID.toString(), playerItems));
		containersConfig.save(IOException::printStackTrace);
	}
}
