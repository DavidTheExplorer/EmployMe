package com.pseudonova.employme.messages;

import static com.pseudonova.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.ChatColor.RED;

import org.bukkit.command.CommandSender;

public enum Message
{
	JOB_ADDED_TO_BOARD("&aYour &e%s &aOffer was added to the &eJobs Board&a."),
	ONE_INVENTORY_REWARD_NEEDED(RED + "You have to put at least 1 reward in your Inventory!");
	
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