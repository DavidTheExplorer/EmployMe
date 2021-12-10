package dte.employme.board.displayers;

import static dte.employme.utils.InventoryUtils.createWall;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dte.employme.board.JobBoard;
import dte.employme.items.ItemFactory;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final Comparator<Job> jobsOrderComparator;
	private final ItemFactory itemFactory;
	
	private static final Map<Inventory, JobBoard> INVENTORIES = new HashMap<>();
	
	public InventoryBoardDisplayer(Comparator<Job> jobsOrderComparator, ItemFactory itemFactory) 
	{
		this.jobsOrderComparator = jobsOrderComparator;
		this.itemFactory = itemFactory;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, createWall(Material.GRAY_STAINED_GLASS_PANE));
		
		jobBoard.getOfferedJobs().stream()
		.sorted(this.jobsOrderComparator)
		.map(job -> this.itemFactory.createOfferIcon(jobBoard, job, player))
		.forEach(inventory::addItem);

		INVENTORIES.put(inventory, jobBoard);
		player.openInventory(inventory);
	}
	
	public static Optional<JobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES.get(inventory));
	}
}
