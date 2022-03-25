package dte.employme.items;

import static dte.employme.messages.MessageKey.CURRENCY_SYMBOL;
import static dte.employme.messages.MessageKey.JOB_ICON_ENCHANT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_GOAL_INSTRUCTIONS;
import static dte.employme.messages.MessageKey.JOB_ICON_ITEMS_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_MONEY_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_NAME;
import static dte.employme.messages.Placeholders.EMPLOYER;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.ITEMS_AMOUNT;
import static dte.employme.messages.Placeholders.MONEY_PAYMENT;
import static dte.employme.utils.ChatColorUtils.colorize;
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
import dte.employme.messages.Placeholders;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.RomanNumeralsConverter;

public class JobIconFactory
{
	private final MessageService messageService;
	
	public JobIconFactory(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	public ItemStack createFor(Job job) 
	{
		List<String> lore = new ArrayList<>();
		lore.addAll(this.messageService.getMessage(JOB_ICON_GOAL_INSTRUCTIONS)
				.inject(GOAL, ItemStackUtils.describe(job.getGoal()))
				.toList());
		lore.addAll(getGoalEnchantmentsLore(job.getGoal()));
		lore.add(" ");
		lore.add(describe(job.getReward()));
		lore.add(" ");

		return new ItemBuilder(job.getGoal().getType())
				.named(this.messageService.getMessage(JOB_ICON_NAME).inject(EMPLOYER, job.getEmployer().getName()).first())
				.withItemFlags(HIDE_ATTRIBUTES)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}

	private List<String> getGoalEnchantmentsLore(ItemStack goal)
	{
		List<String> lore = new ArrayList<>();
		Map<Enchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(goal);

		if(enchantments.isEmpty())
			return lore;

		lore.add(" ");
		lore.add(this.messageService.getMessage(JOB_ICON_ENCHANT_DESCRIPTION).first());

		enchantments.forEach((enchantment, level) -> 
		{
			String enchantmentName = EnchantmentUtils.getDisplayName(enchantment);
			String romanLevel = RomanNumeralsConverter.convert(level);

			lore.add(colorize(String.format("&f✶ &a%s %s", enchantmentName, romanLevel)));
		});

		return lore;
	}

	private String describe(Reward reward)
	{
		if(reward instanceof MoneyReward)
			return this.messageService.getMessage(JOB_ICON_MONEY_PAYMENT_DESCRIPTION)
					.inject(MONEY_PAYMENT, String.format("%.2f", ((MoneyReward) reward).getPayment()))
					.inject(Placeholders.CURRENCY_SYMBOL, this.messageService.getMessage(CURRENCY_SYMBOL).first())
					.first();

		else if(reward instanceof ItemsReward)
			return this.messageService.getMessage(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION)
					.inject(ITEMS_AMOUNT, String.valueOf(((ItemsReward) reward).getItems().size()))
					.first();

		else
			throw new IllegalStateException(String.format("The provided items reward cannot be described! (%s)", reward));
	}
}