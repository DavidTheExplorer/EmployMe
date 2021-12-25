package dte.employme.board.displayers;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.items.JobBasicIcon;
import dte.employme.items.JobItemUtils;
import dte.employme.job.Job;
import dte.employme.job.service.JobService;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.items.ItemBuilder;

public class InventoryBoardDisplayer implements JobBoardDisplayer
{
	private final Comparator<Job> orderComparator;
	private final JobService jobService;
	
	private static final Map<Inventory, JobBoard> INVENTORIES = new HashMap<>();
	
	public InventoryBoardDisplayer(Comparator<Job> orderComparator, JobService jobService) 
	{
		this.orderComparator = orderComparator;
		this.jobService = jobService;
	}
	
	@Override
	public void display(Player player, JobBoard jobBoard) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, createWall(Material.GRAY_STAINED_GLASS_PANE));
		
		jobBoard.getOfferedJobs().stream()
		.sorted(this.orderComparator)
		.map(job -> createOfferIcon(jobBoard, job, player))
		.forEach(inventory::addItem);

		INVENTORIES.put(inventory, jobBoard);
		player.openInventory(inventory);
	}
	
	public static Optional<JobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES.get(inventory));
	}
	
	private ItemStack createOfferIcon(JobBoard jobBoard, Job job, Player player) 
	{
		ItemStack basicIcon = JobBasicIcon.create(job);
		boolean finished = this.jobService.hasFinished(player, job);
		
		//add the status and ID to the lore
		String separator = createSeparationLine(finished ? WHITE : DARK_RED, finished ? 25 : 29);
		String finishMessage = finished ? (bold(GREEN) +  "Click to Finish!") : (RED + "You didn't complete this Job.");
		
		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(separator);
		lore.add(StringUtils.repeat(" ", finished ? 8 : 4) + finishMessage);
		lore.add(separator);
		lore.add(JobItemUtils.createIDLoreLine(job, jobBoard));

		return new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}
}
