package dte.employme.items;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.rewards.Reward;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.RomanNumeralsConverter;

public class JobBasicIcon
{
	public static ItemStack of(Job job) 
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
	
	private static List<String> getGoalEnchantmentsLore(ItemStack goal)
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
		String description = "&6&n&lPayment&6: ";

		if(reward instanceof MoneyReward)
			description += String.format("&f%.2f$", ((MoneyReward) reward).getPayment());

		else if(reward instanceof ItemsReward)
			description += String.format("&fRight Click to preview items(%d).", ((ItemsReward) reward).getItems().size());

		else
			throw new IllegalStateException(String.format("The provided items reward cannot be described! (%s)", reward));
		
		return colorize(description);
	}
}
