package com.pseudonova.employme.board.inventory;

import static com.pseudonova.employme.utils.ChatColorUtils.bold;
import static com.pseudonova.employme.utils.ChatColorUtils.colorize;
import static com.pseudonova.employme.utils.ChatColorUtils.createSeparationLine;
import static com.pseudonova.employme.utils.ChatColorUtils.underlined;
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
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.pseudonova.employme.board.AbstractJobBoard;
import com.pseudonova.employme.goal.Goal;
import com.pseudonova.employme.goal.ItemGoal;
import com.pseudonova.employme.job.Job;
import com.pseudonova.employme.utils.InventoryUtils;
import com.pseudonova.employme.utils.items.builder.ItemBuilder;

public class InventoryJobBoard extends AbstractJobBoard
{
	private final BiMap<String, Job> jobByID = HashBiMap.create(); //TODO: put this map in AbstractJobBoard

	private static final Map<Inventory, InventoryJobBoard> INVENTORIES_BOARDS = new HashMap<>();

	@Override
	public void addJob(Job job) 
	{
		super.addJob(job);

		this.jobByID.put(generateID(), job);
	}

	@Override
	public void removeJob(Job job) 
	{
		super.removeJob(job);

		this.jobByID.inverse().remove(job);
	}

	@Override
	public void showTo(Player player) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Available Jobs");
		InventoryUtils.buildWalls(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
		INVENTORIES_BOARDS.put(inventory, this);

		//add the jobs to the inventory; the icons depend on whether the player finished the job
		this.offeredJobs.stream()
		.map(job -> createIconFor(job, player))
		.forEach(inventory::addItem);

		player.openInventory(inventory);
	}

	public Optional<Job> getJobByID(String id)
	{
		return Optional.ofNullable(this.jobByID.get(id));
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

	private String generateID() 
	{
		String id;

		do 
		{
			id = RandomStringUtils.randomAlphanumeric(22);
		}
		while(this.jobByID.containsKey(id));

		return id;
	}



	/*
	 * Job Icon Creation 
	 */
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
		lore = ArrayUtils.add(lore, colorize(String.format("&7ID: %s", this.jobByID.inverse().get(job))));

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