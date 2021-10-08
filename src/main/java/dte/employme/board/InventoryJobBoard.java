package dte.employme.board;

import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.Comparator.comparing;

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
	private Comparator<Job> jobsComparator;
	
	public static final Comparator<Job> 
	ORDER_BY_EMPLOYER_NAME = comparing(job -> job.getEmployer().getName().toLowerCase()),
	ORDER_BY_GOAL = comparing(job -> job.getGoal().getType().name());
	
	private static final Map<Inventory, InventoryJobBoard> INVENTORIES_BOARDS = new HashMap<>();
	
	public InventoryJobBoard(Comparator<Job> jobsComparator) 
	{
		this.jobsComparator = jobsComparator;
	}
	
	public InventoryJobBoard() 
	{
		this(ORDER_BY_EMPLOYER_NAME);
	}
	
	@Override
	public void showTo(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, createWall(Material.GRAY_STAINED_GLASS_PANE));
		
		//add the jobs of this board
		getOfferedJobs().stream()
		.sorted(this.jobsComparator)
		.map(job -> ItemFactory.createOfferIcon(this, job, player))
		.forEach(inventory::addItem);
		
		INVENTORIES_BOARDS.put(inventory, this);
		player.openInventory(inventory);
	}
	
	public void setJobsOrder(Comparator<Job> jobsComparator) 
	{
		this.jobsComparator = jobsComparator;
	}

	public static Optional<InventoryJobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES_BOARDS.get(inventory));
	}
}