package dte.employme.inventories;

import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.stream.Collectors.toMap;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.items.ItemFactory;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.items.ItemBuilder;

public class InventoryFactory 
{
	private final ItemFactory itemFactory;
	private final JobBoard globalJobBoard;
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();

	private ConfigFile itemsContainersConfig, rewardsContainersConfig;

	//cached menus
	private Inventory jobCreationInventory;

	private static final Set<Integer> INVALID_SLOTS = Sets.newHashSet(43, 44, 52, 53);

	public InventoryFactory(ItemFactory itemFactory, JobBoard globalJobBoard) 
	{
		this.itemFactory = itemFactory;
		this.globalJobBoard = globalJobBoard;
	}

	/*
	 * Menus
	 */
	public Inventory getCreationMenu(Player employer)
	{
		if(this.jobCreationInventory == null)
			this.jobCreationInventory = createJobCreationMenu();

		return this.jobCreationInventory;
	}

	public Inventory getDeletionMenu(Player employer)
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Select Jobs to Delete");

		this.globalJobBoard.getJobsOfferedBy(employer.getUniqueId()).stream()
		.map(job -> this.itemFactory.createDeletionIcon(this.globalJobBoard, job))
		.forEach(inventory::addItem);

		InventoryUtils.fillEmptySlots(inventory, InventoryUtils.createWall(Material.BLACK_STAINED_GLASS_PANE));

		return inventory;
	}

	private Inventory createJobCreationMenu()
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Create a new Job");

		inventory.setItem(11, new ItemBuilder(Material.GOLD_INGOT)
				.named(GOLD + "Money Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay a certain amount of money.")
				.createCopy());

		inventory.setItem(15, new ItemBuilder(Material.CHEST)
				.named(AQUA + "Items Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy());

		InventoryUtils.fillEmptySlots(inventory, createWall(Material.BLACK_STAINED_GLASS_PANE));

		return inventory;
	}


	/*
	 * Players Containers
	 */
	public Inventory getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> createRewardsContainer());
	}


	public Inventory getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> createItemsContainer());
	}

	public void loadPlayersContainers() 
	{
		//items containers
		this.itemsContainersConfig = ConfigFile.byPath("containers" + File.separator + "items containers");
		this.itemsContainersConfig.createIfAbsent(IOException::printStackTrace);
		loadContainers(this.itemsContainersConfig, this.itemsContainers);

		//rewards containers
		this.rewardsContainersConfig = ConfigFile.byPath("containers" + File.separator + "rewards containers");
		this.rewardsContainersConfig.createIfAbsent(IOException::printStackTrace);
		loadContainers(this.rewardsContainersConfig, this.rewardsContainers);
	}

	public void savePlayersContainers() 
	{
		saveContainers(this.itemsContainers, this.itemsContainersConfig);
		saveContainers(this.rewardsContainers, this.rewardsContainersConfig);
	}
	
	private static void loadContainers(ConfigFile containersConfig, Map<UUID, Inventory> containersMap) 
	{
		containersConfig.getConfig().getKeys(false).stream()
		.map(UUID::fromString)
		.forEach(playerUUID -> 
		{
			Map<Integer, ItemStack> playerItems = containersConfig.getConfig().getConfigurationSection(playerUUID.toString())
					.getKeys(false).stream()
					.map(Integer::parseInt)
					.collect(toMap(Function.identity(), index -> containersConfig.getConfig().getItemStack(playerUUID + "." + index)));
			
			Inventory container = createItemsContainer();
			playerItems.forEach((slot, item) -> container.setItem(slot, item));
			
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

	private static Inventory createItemsContainer() 
	{
		return new ContainerBuilder()
				.of("Items")
				.withHelp("When someone completes one of your jobs,", "The items they got for you are stored here.")
				.build();
	}

	private static Inventory createRewardsContainer() 
	{
		return new ContainerBuilder()
				.of("Rewards")
				.withHelp("This is where Reward Items are stored", "after you complete a job that pays them.")
				.build();
	}

	private static class ContainerBuilder
	{
		String title;
		String[] helpDescription;
		Collection<ItemStack> initialItems = new ArrayList<>();

		public ContainerBuilder of(String subject) 
		{
			this.title = String.format("Claim your %s:", subject);
			return this;
		}

		public ContainerBuilder withHelp(String... helpDescription) 
		{
			this.helpDescription = helpDescription;
			return this;
		}

		public Inventory build() 
		{
			Inventory inventory = Bukkit.createInventory(null, 9 * 6, this.title);

			inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
			inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
			inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));

			inventory.setItem(53, new ItemBuilder(Material.BOOK)
					.named(GREEN + "Help")
					.withLore(Arrays.stream(this.helpDescription).map(line -> WHITE + line).toArray(String[]::new))
					.createCopy());

			this.initialItems.forEach(inventory::addItem);

			return inventory;
		}
	}
}
