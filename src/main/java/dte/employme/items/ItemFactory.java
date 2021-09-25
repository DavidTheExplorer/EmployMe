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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.board.JobBoard;
import dte.employme.goal.ItemGoal;
import dte.employme.job.Job;
import dte.employme.job.service.JobService;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.visitors.goal.InventoryGoalDescriptor;
import dte.employme.visitors.reward.InventoryRewardDescriptor;

public class ItemFactory
{
	//Container of factory methods
	private ItemFactory(){}
	
	private static JobService jobService;
	
	public static void setup(JobService jobService) 
	{
		ItemFactory.jobService = jobService;
	}

	/*
	 * Jobs
	 */
	public static ItemStack createBasicIcon(Job job) 
	{
		verifySetup();
		
		//lore
		List<String> lore = new ArrayList<>();
		lore.add(underlined(AQUA) + "Description" + AQUA + ":");
		lore.add(WHITE + "I need " + job.getGoal().accept(InventoryGoalDescriptor.INSTANCE));
		lore.add(" ");
		lore.addAll(job.getReward().accept(InventoryRewardDescriptor.INSTANCE));

		return new ItemBuilder(getGoalMaterial(job))
				.named(GREEN + job.getEmployer().getName() + "'s Offer")
				.withItemFlags(HIDE_ATTRIBUTES)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	public static ItemStack createOfferIcon(JobBoard jobBoard, Job job, Player player) 
	{
		verifySetup();
		
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

	public static ItemStack createDeletionIcon(JobBoard jobBoard, Job job) 
	{
		verifySetup();
		
		return new ItemBuilder(createBasicIcon(job))
				.ofType(Material.BARRIER)
				.addToLore(true,
						createSeparationLine(GRAY, 23),
						bold(DARK_RED) + "Click to Delete!",
						createSeparationLine(GRAY, 23),
						createIDLine(job, jobBoard))
				.createCopy();
	}

	public static Optional<String> getJobID(ItemStack jobIcon)
	{
		verifySetup();
		
		if(!jobIcon.hasItemMeta() || !jobIcon.getItemMeta().hasLore() || jobIcon.getItemMeta().getLore().isEmpty())
			return Optional.empty();

		List<String> lore = jobIcon.getItemMeta().getLore();
		String lastLine = lore.get(lore.size()-1);

		return Optional.of(ChatColor.stripColor(lastLine.substring(6)));
	}


	private static List<String> createJobStatusLore(Job job, Player player) 
	{
		verifySetup();
		
		boolean finished = jobService.hasFinished(job, player);
		ChatColor lineColor = finished ? WHITE : DARK_RED;

		return Lists.newArrayList(
				createSeparationLine(lineColor, 23),
				finished ? (StringUtils.repeat(' ', 6) + bold(GREEN) +  "Click to Finish!") : (RED + "You didn't complete this Job."),
						createSeparationLine(lineColor, 23)
				);
	}

	public static Material getGoalMaterial(Job job) 
	{
		verifySetup();
		
		if(job.getGoal() instanceof ItemGoal)
			return ((ItemGoal) job.getGoal()).getItem().getType();
		
		return Material.BOOK;
	}

	private static String createIDLine(Job job, JobBoard jobBoard)
	{
		return colorize(String.format("&7ID: %s", jobBoard.getJobID(job).get()));
	}
	
	private static void verifySetup() 
	{
		Validate.notNull(jobService, "ItemFactory must be initialized via #setup()");
	}
}
