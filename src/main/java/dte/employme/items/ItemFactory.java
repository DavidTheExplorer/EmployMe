package dte.employme.items;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.colorize;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.rewards.Reward;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.RomanNumeralsConverter;

public class ItemFactory
{
	/*
	 * Jobs
	 */
	public ItemStack createBasicIcon(Job job) 
	{
		List<String> lore = new ArrayList<>();
		lore.add(bold(AQUA) + "Goal: " + WHITE + "I need " + AQUA + AQUA + ItemStackUtils.describe(job.getGoal()) + WHITE + ".");
		lore.addAll(getGoalEnchantmentsLore(job.getGoal()));
		lore.add(" ");
		lore.add(describe(job.getReward()));
		lore.add(" ");

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
		lore.addAll(createJobStatusLore(job, player));
		lore.add(createIDLine(job, jobBoard));

		return new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	public ItemStack createDeletionIcon(JobBoard jobBoard, Job job) 
	{
		return new ItemBuilder(createBasicIcon(job))
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
		String separator = createSeparationLine(finished ? WHITE : DARK_RED, finished ? 25 : 29);
		String finishMessage = finished ? (bold(GREEN) +  "Click to Finish!") : (RED + "You didn't complete this Job.");

		return Lists.newArrayList(
				separator,
				StringUtils.repeat(" ", finished ? 8 : 4) + finishMessage, 
				separator
				);
	}

	private static String createIDLine(Job job, JobBoard jobBoard)
	{
		return colorize(String.format("&7ID: %s", jobBoard.getJobID(job).get()));
	}

	private List<String> getGoalEnchantmentsLore(ItemStack goal)
	{
		List<String> lore = new ArrayList<>();
		Map<Enchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(goal);

		if(enchantments.isEmpty())
			return lore;

		lore.add(" ");
		lore.add(String.format(LIGHT_PURPLE + "Enchant " + WHITE + "%s with:", goal.getAmount() == 1 ? "it" : "them"));

		enchantments.forEach((enchantment, level) -> 
		{
			String enchantmentName = EnchantmentUtils.getDisplayName(enchantment);
			String romanLevel = RomanNumeralsConverter.convert(level);

			lore.add(colorize(String.format("&f✶ &a%s %s", enchantmentName, romanLevel)));
		});

		return lore;
	}

	private static String describe(Reward reward)
	{
		String description = colorize("&6&n&lPayment&6: ");

		if(reward instanceof MoneyReward)
			description += colorize(String.format("&f%.2f$", ((MoneyReward) reward).getPayment()));

		else if(reward instanceof ItemsReward)
			description += WHITE + "Click to view Items.";

		else
			throw new IllegalStateException(String.format("The provided reward cannot be described! (%s)", reward));
		
		return description;
	}
}