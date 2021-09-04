package dte.employme.board;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.colorize;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.ChatColorUtils.underlined;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.inventory.GoalDescriptor;
import dte.employme.board.inventory.RewardDescriptor;
import dte.employme.goal.Goal;
import dte.employme.goal.ItemGoal;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.items.builder.ItemBuilder;

public class InventoryJobBoard extends AbstractJobBoard
{
	private static final Map<Inventory, InventoryJobBoard> INVENTORIES_BOARDS = new HashMap<>();

	@Override
	public void showTo(Player player) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, InventoryUtils.createWall(Material.GRAY_STAINED_GLASS_PANE));
		INVENTORIES_BOARDS.put(inventory, this);

		//add the jobs to the inventory
		getOfferedJobs().stream()
		.map(job -> createIconFor(job, player))
		.forEach(inventory::addItem);

		player.openInventory(inventory);
	}

	public Optional<String> getJobID(ItemStack jobItem)
	{
		if(!jobItem.hasItemMeta() || !jobItem.getItemMeta().hasLore() || jobItem.getItemMeta().getLore().isEmpty())
			return Optional.empty();

		List<String> lore = jobItem.getItemMeta().getLore();
		String lastLine = lore.get(lore.size()-1);

		return Optional.of(ChatColor.stripColor(lastLine.substring(6)));
	}

	public static Optional<InventoryJobBoard> getRepresentedBoard(Inventory inventory)
	{
		return Optional.ofNullable(INVENTORIES_BOARDS.get(inventory));
	}
	
	private ItemStack createIconFor(Job job, Player player) 
	{
		//lore
		String[] lore = {};
		lore = ArrayUtils.add(lore, underlined(AQUA) + "Description" + AQUA + ":");
		lore = ArrayUtils.add(lore, WHITE + "I need " + job.getGoal().accept(GoalDescriptor.INSTANCE));
		lore = ArrayUtils.add(lore, " ");
		lore = ArrayUtils.addAll(lore, job.getReward().accept(RewardDescriptor.INSTANCE));
		lore = ArrayUtils.add(lore, " ");
		lore = ArrayUtils.addAll(lore, createStatusLore(job, player));
		lore = ArrayUtils.add(lore, colorize(String.format("&7ID: %s", getJobID(job).get())));

		return new ItemBuilder(getJobMaterial(job), GREEN + job.getEmployer().getName() + "'s Offer")
				.newLore(lore)
				.createCopy();
	}

	private static String[] createStatusLore(Job job, Player player) 
	{
		boolean finished = job.hasFinished(player);
		ChatColor lineColor = finished ? WHITE : DARK_RED;

		return new String[] 
				{
						createSeparationLine(lineColor, 23),
						finished ? (StringUtils.repeat(' ', 6) + bold(GREEN) +  "Click to Finish!") : (RED + "You didn't complete this Job."),
								createSeparationLine(lineColor, 23)
				};
	}

	private static Material getJobMaterial(Job job) 
	{
		Goal goal = job.getGoal();
		
		if(goal instanceof ItemGoal)
		{
			ItemGoal itemGoal = (ItemGoal) goal;
			
			return itemGoal.getItem().getType();
		}
		return Material.BOOK;
	}
}