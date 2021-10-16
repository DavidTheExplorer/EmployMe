package dte.employme.containers.service;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import dte.employme.config.ConfigFile;
import dte.employme.containers.PlayerContainer;

public class SimplePlayerContainerService implements PlayerContainerService
{
	private final Map<UUID, PlayerContainer> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();

	private ConfigFile itemsContainersConfig, rewardsContainersConfig;
	
	private static final Set<Integer> INVALID_SLOTS = Sets.newHashSet(43, 44, 52, 53);
	
	@Override
	public PlayerContainer getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> PlayerContainer.ofItems());
	}

	@Override
	public PlayerContainer getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> PlayerContainer.ofRewards());
	}

	@Override
	public void loadContainers() 
	{
		//items containers
		this.itemsContainersConfig = ConfigFile.byPath("containers" + File.separator + "items containers");
		this.itemsContainersConfig.createIfAbsent(IOException::printStackTrace);
		loadContainers(this.itemsContainersConfig, this.itemsContainers, PlayerContainer::ofItems);

		//rewards containers
		this.rewardsContainersConfig = ConfigFile.byPath("containers" + File.separator + "rewards containers");
		this.rewardsContainersConfig.createIfAbsent(IOException::printStackTrace);
		loadContainers(this.rewardsContainersConfig, this.rewardsContainers, PlayerContainer::ofRewards);
	}

	@Override
	public void saveContainers() 
	{
		saveContainers(this.itemsContainers, this.itemsContainersConfig);
		saveContainers(this.rewardsContainers, this.rewardsContainersConfig);
	}

	private static void loadContainers(ConfigFile containersConfig, Map<UUID, PlayerContainer> containersMap, Supplier<PlayerContainer> containerCreator) 
	{
		containersConfig.getConfig().getKeys(false).stream()
		.map(UUID::fromString)
		.forEach(playerUUID -> 
		{
			Map<Integer, ItemStack> playerItems = containersConfig.getConfig().getConfigurationSection(playerUUID.toString())
					.getKeys(false).stream()
					.map(Integer::parseInt)
					.collect(toMap(Function.identity(), index -> containersConfig.getConfig().getItemStack(playerUUID + "." + index)));
			
			
			PlayerContainer container = containerCreator.get();
			playerItems.forEach(container::setItem);

			containersMap.put(playerUUID, container);
		});
	}

	private static void saveContainers(Map<UUID, PlayerContainer> containersMap, ConfigFile containersConfig) 
	{
		Map<UUID, Map<Integer, ItemStack>> playersItems = containersMap.entrySet().stream()
				.collect(toMap(Entry::getKey, entry -> 
				{
					PlayerContainer container = entry.getValue();

					return IntStream.range(0, container.size())
							.filter(i -> !INVALID_SLOTS.contains(i))
							.filter(i -> container.getItem(i) != null)
							.boxed()
							.collect(toMap(Function.identity(), container::getItem));
				}));

		playersItems.forEach((playerUUID, playerItems) -> containersConfig.getConfig().set(playerUUID.toString(), playerItems));
		containersConfig.save(IOException::printStackTrace);
	}
}
