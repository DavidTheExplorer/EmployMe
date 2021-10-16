package dte.employme.board;

import static dte.employme.utils.InventoryUtils.createWall;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dte.employme.items.ItemFactory;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;

public class InventoryJobBoard extends AbstractJobBoard
{
	private final ItemFactory itemFactory;
	private final Comparator<Job> orderComparator;
	
	private static final Map<Inventory, InventoryJobBoard> INVENTORIES_BOARDS = new HashMap<>();
	
	public InventoryJobBoard(ItemFactory itemFactory, Comparator<Job> orderComparator) 
	{
		this.itemFactory = itemFactory;
		this.orderComparator = orderComparator;
	}
	
	@Override
	public void showTo(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, createWall(Material.GRAY_STAINED_GLASS_PANE));
		
		//add the jobs of this board
		getOfferedJobs().stream()
		.sorted(this.orderComparator)
		.map(job -> this.itemFactory.createOfferIcon(this, job, player))
		.forEach(inventory::addItem);
		
		INVENTORIES_BOARDS.put(inventory, this);
		player.openInventory(inventory);
	}

	public static Optional<InventoryJobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES_BOARDS.get(inventory));
	}
}