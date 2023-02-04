package dte.employme.items;

import static dte.employme.messages.MessageKey.CURRENCY_SYMBOL;
import static dte.employme.messages.MessageKey.JOB_ICON_CUSTOM_GOAL_INSTRUCTIONS;
import static dte.employme.messages.MessageKey.JOB_ICON_ENCHANT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_GOAL_INSTRUCTIONS;
import static dte.employme.messages.MessageKey.JOB_ICON_ITEMS_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_MONEY_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_NAME;
import static dte.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import dte.employme.items.providers.ItemProvider;
import dte.employme.items.providers.VanillaProvider;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.MoneyReward;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.RomanNumeralsConverter;

public class JobIcon
{
	public static ItemStack create(Job job, MessageService messageService) 
	{
		String name = messageService.getMessage(JOB_ICON_NAME)
				.inject("employer", job.getEmployer().getName())
				.first();
		
		List<String> lore = new ArrayList<>();
		lore.addAll(getGoalInstructions(messageService, job));
		lore.addAll(getGoalEnchantmentsLore(job.getGoal(), messageService));
		lore.add(" ");
		lore.add(describeReward(job.getReward(), messageService));
		lore.add(" ");

		return new ItemBuilder(job.getGoal().getType())
				.named(name)
				.withItemFlags(HIDE_ATTRIBUTES)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	private static List<String> getGoalInstructions(MessageService messageService, Job job)
	{
		ItemProvider goalProvider = job.getGoalProvider();

		return messageService.getMessage(goalProvider instanceof VanillaProvider ? JOB_ICON_GOAL_INSTRUCTIONS : JOB_ICON_CUSTOM_GOAL_INSTRUCTIONS)
				.inject("goal", goalProvider.getDisplayName(job.getGoal()))
				.inject("item provider", goalProvider.getName())
				.toList();
	}

	private static List<String> getGoalEnchantmentsLore(ItemStack goal, MessageService messageService)
	{
		Map<Enchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(goal);

		if(enchantments.isEmpty())
			return new ArrayList<>();

		List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.add(messageService.getMessage(JOB_ICON_ENCHANT_DESCRIPTION).first());

		enchantments.forEach((enchantment, level) -> 
		{
			String enchantmentName = EnchantmentUtils.getDisplayName(enchantment);
			String romanLevel = RomanNumeralsConverter.convert(level);

			lore.add(colorize(String.format("&f✶ &a%s %s", enchantmentName, romanLevel)));
		});

		return lore;
	}

	private static String describeReward(Reward reward, MessageService messageService)
	{
		if(reward instanceof MoneyReward moneyReward)
			return messageService.getMessage(JOB_ICON_MONEY_PAYMENT_DESCRIPTION)
					.inject("money payment", String.format("%.2f", moneyReward.getPayment()))
					.inject("currency symbol", messageService.getMessage(CURRENCY_SYMBOL).first())
					.first();

		else if(reward instanceof ItemsReward itemsReward)
			return messageService.getMessage(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION)
					.inject("items amount", itemsReward.getItems().size())
					.first();

		else
			throw new IllegalStateException(String.format("The provided items reward cannot be described! (%s)", reward));
	}
}