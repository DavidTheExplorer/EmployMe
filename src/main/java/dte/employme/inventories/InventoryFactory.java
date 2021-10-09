package dte.employme.inventories;

import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dte.employme.board.JobBoard;
import dte.employme.items.ItemFactory;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.items.ItemBuilder;

public class InventoryFactory 
{
	private final JobBoard globalJobBoard;
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();
	private Inventory jobCreationInventory;
	
	public InventoryFactory(JobBoard globalJobBoard) 
	{
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
		.map(job -> ItemFactory.createDeletionIcon(this.globalJobBoard, job))
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
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> createContainer("Claim your Rewards:", 
				"This is where Reward Items are stored", 
				"after you complete a job that pays them."));
	}


	public Inventory getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> createContainer("Claim your Items:",
				"When someone completes one of your jobs,", 
				"The items they got for you are stored here."));
	}

	private static Inventory createContainer(String title, String... bookDescription) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, title);

		inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));

		inventory.setItem(53, new ItemBuilder(Material.BOOK)
				.named(GREEN + "Help")
				.withLore(Arrays.stream(bookDescription).map(line -> WHITE + line).toArray(String[]::new))
				.createCopy());

		return inventory;
	}
}
