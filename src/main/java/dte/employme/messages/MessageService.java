package dte.employme.messages;

import org.bukkit.command.CommandSender;

import dte.employme.EmployMe;

@FunctionalInterface
public interface MessageService
{
	String getMessage(MessageKey key, Placeholders placeholders);
	
	default String getGeneralMessage(MessageKey key, Placeholders placeholders) 
	{
		return String.format("%s %s", EmployMe.CHAT_PREFIX, getMessage(key, placeholders));
	}
	
	
	/*
	 * Delegation methods without placeholders
	 */
	default String getMessage(MessageKey key) 
	{
		return getMessage(key, Placeholders.NONE);
	}
	
	default String getGeneralMessage(MessageKey key)
	{
		return getGeneralMessage(key, Placeholders.NONE);
	}
	
	
	/*
	 * Methods that simply send messages
	 */
	default void sendTo(CommandSender sender, MessageKey key, Placeholders placeholders) 
	{
		sender.sendMessage(getMessage(key, placeholders));
	}
	
	default void sendTo(CommandSender sender, MessageKey key) 
	{
		sendTo(sender, key, Placeholders.NONE);
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey key, Placeholders placeholders) 
	{
		sender.sendMessage(getGeneralMessage(key, placeholders));
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey key) 
	{
		sendGeneralMessage(sender, key, Placeholders.NONE);
	}
}