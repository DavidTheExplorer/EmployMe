package dte.employme.items;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.colorize;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.ChatColorUtils.underlined;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.visitors.reward.InventoryRewardDescriptor;

public class ItemFactory
{
	/*
	 * Jobs
	 */
	public ItemStack createBasicIcon(Job job) 
	{
		//lore
		List<String> lore = new ArrayList<>();
		lore.add(underlined(AQUA) + "Description" + AQUA + ":");
		lore.add(WHITE + "I need " + AQUA + ItemStackUtils.describe(job.getGoal()) + WHITE + ".");
		lore.add(" ");
		lore.addAll(job.getReward().accept(InventoryRewardDescriptor.INSTANCE));

		return new ItemBuilder(job.getGoal().getType())
				.named(GREEN + job.getEmployer().getName() + "'s Offer")
				.withItemFlags(HIDE_ATTRIBUTES)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	public ItemStack createOfferIcon(JobBoard jobBoard, Job job, Player player) 
	{
		ItemStack basicIcon = createBasicIcon(job);

		//add the status and ID to the lore
		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(" ");
		lore.addAll(createJobStatusLore(job, player));
		lore.add(createIDLine(job, jobBoard));

		return new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	public ItemStack createDeletionIcon(JobBoard jobBoard, Job job) 
	{
		return new ItemBuilder(createBasicIcon(job))
				.named(" ")
				.ofType(Material.BARRIER)
				.addToLore(true,
						createSeparationLine(GRAY, 23),
						bold(DARK_RED) + "Click to Delete!",
						createSeparationLine(GRAY, 23),
						createIDLine(job, jobBoard))
				.createCopy();
	}

	public Optional<String> getJobID(ItemStack jobIcon)
	{
		if(!jobIcon.hasItemMeta() || !jobIcon.getItemMeta().hasLore() || jobIcon.getItemMeta().getLore().isEmpty())
			return Optional.empty();

		List<String> lore = jobIcon.getItemMeta().getLore();
		String lastLine = lore.get(lore.size()-1);

		return Optional.of(ChatColor.stripColor(lastLine.substring(6)));
	}


	private List<String> createJobStatusLore(Job job, Player player) 
	{
		boolean finished = job.hasFinished(player);
		ChatColor lineColor = finished ? WHITE : DARK_RED;
		
		return Lists.newArrayList(
				createSeparationLine(lineColor, 23),
				finished ? (StringUtils.repeat(" ", 6) + bold(GREEN) +  "Click to Finish!") : (RED + "You didn't complete this Job."),
						createSeparationLine(lineColor, 23)
				);
	}

	private static String createIDLine(Job job, JobBoard jobBoard)
	{
		return colorize(String.format("&7ID: %s", jobBoard.getJobID(job).get()));
	}
}
