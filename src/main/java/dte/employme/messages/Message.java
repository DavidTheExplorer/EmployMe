package dte.employme.messages;

import static dte.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.command.CommandSender;

public enum Message
{
	//Jobs
	JOB_ADDED_TO_BOARD("&aYour &e%s &aOffer was added to the &eJobs Board&a."),
	JOB_SUCCESSFULLY_COMPLETED(GREEN + "You successfully completed a %s Job!"),

	//Rewards
	ONE_INVENTORY_REWARD_NEEDED(RED + "You have to put at least 1 reward in your Inventory!"),
	MONEY_PAYMENT_AMOUNT_QUESTION("&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%.2f&6$&f)"),

	//Goals
	ITEM_GOAL_FORMAT_QUESTION(WHITE + "Which " + GREEN + "item" + WHITE + " do you need? Reply with the following format: " + AQUA + "itemName:amount"),
	ITEM_GOAL_INVALID_FORMAT(RED + "Invalid Format!"),

	//Rewards
	MONEY_REWARD_ERROR_NEGATIVE(RED + "Can't create a Money Reward that pays nothing or less!"),
	MONEY_REWARD_NOT_ENOUGH(RED + "You can't offer an amount of money that you don't have!"),
	MONEY_REWARD_NOT_A_NUMBER(RED + "Payment has to be a Positive Integer!"),

	//General
	GENERAL_PREFIX(DARK_GREEN + "[" + GREEN + "EmployMe" + DARK_GREEN + "] "),
	MUST_NOT_BE_CONVERSING(RED + "You have to finish your current conversation.");

	private final String template;

	Message(String template)
	{
		this.template = template;
	}

	public String getTemplate() 
	{
		return this.template;
	}

	public void sendTo(CommandSender sender, Object... args) 
	{
		sender.sendMessage(inject(args));
	}

	public String inject(Object... args) 
	{
		return colorize(String.format(this.template, args));
	}
	
	public static void sendGeneralMessage(CommandSender sender, String message) 
	{
		sender.sendMessage(String.format("%s%s", GENERAL_PREFIX, message));
	}
	
	public static void sendGeneralMessage(CommandSender sender, Message message, Object... args) 
	{
		sendGeneralMessage(sender, message.inject(args));
	}

	@Override
	public String toString() 
	{
		return this.template;
	}
}