package dte.employme.board;

import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.items.ItemFactory;
import dte.employme.utils.InventoryUtils;

public class InventoryJobBoard extends AbstractJobBoard
{
	private static final Map<Inventory, InventoryJobBoard> INVENTORIES_BOARDS = new HashMap<>();

	@Override
	public void showTo(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, createWall(Material.GRAY_STAINED_GLASS_PANE));
		createJobsIcons(player).forEach(inventory::addItem);
		
		INVENTORIES_BOARDS.put(inventory, this);
		player.openInventory(inventory);
	}

	public static Optional<InventoryJobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES_BOARDS.get(inventory));
	}
	
	private List<ItemStack> createJobsIcons(Player player)
	{
		return getOfferedJobs().stream()
				.map(job -> ItemFactory.createOfferIcon(this, job, player))
				.collect(toList());
	}
}