package com.pseudonova.employme.messages;

import static com.pseudonova.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.ChatColor.RED;

import org.bukkit.command.CommandSender;

public enum Message
{
	//TODO: move all the messages here
	
	//General
	JOB_ADDED_TO_BOARD("&aYour &e%s &aOffer was added to the &eJobs Board&a."),
	ONE_INVENTORY_REWARD_NEEDED(RED + "You have to put at least 1 reward in your Inventory!"),
	
	//Rewards
	MONEY_REWARD_ERROR_NEGATIVE(RED + "Can't create a Money Reward that pays nothing or less!"),
	MONEY_REWARD_NOT_ENOUGH(RED + "You can't offer an amount of money that you don't have!"),
	MONEY_REWARD_NOT_A_NUMBER(RED + "Payment has to be a Positive Integer!");
	
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
		sender.sendMessage(colorize(String.format(this.template, args)));
	}
}