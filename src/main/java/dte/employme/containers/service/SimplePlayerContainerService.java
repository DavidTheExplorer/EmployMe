package dte.employme.containers.service;

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
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import dte.employme.config.ConfigFile;
import dte.employme.containers.PlayerContainerBuilder;

public class SimplePlayerContainerService implements PlayerContainerService
{
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();
	private final ConfigFile itemsContainersConfig, rewardsContainersConfig;
	
	private static final Set<Integer> INVALID_SLOTS = Sets.newHashSet(43, 44, 52, 53);
	
	public SimplePlayerContainerService(ConfigFile itemsContainersConfig, ConfigFile rewardsContainersConfig) 
	{
		this.itemsContainersConfig = itemsContainersConfig;
		this.rewardsContainersConfig = rewardsContainersConfig;
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
	public void loadContainers() 
	{
		loadContainers(this.itemsContainersConfig, this.itemsContainers, SimplePlayerContainerService::createItemsContainer);
		loadContainers(this.rewardsContainersConfig, this.rewardsContainers, SimplePlayerContainerService::createRewardsContainer);
	}

	@Override
	public void saveContainers() 
	{
		saveContainers(this.itemsContainers, this.itemsContainersConfig);
		saveContainers(this.rewardsContainers, this.rewardsContainersConfig);
	}
	
	private static Inventory createItemsContainer() 
	{
		return new PlayerContainerBuilder()
				.of("Items")
				.withHelp("When someone completes one of your jobs,", "The items they got for you are stored here.")
				.build();
	}
	
	public static Inventory createRewardsContainer() 
	{
		return new PlayerContainerBuilder()
				.of("Rewards")
				.withHelp("This is where Reward Items are stored", "after you complete a job that pays them.")
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
