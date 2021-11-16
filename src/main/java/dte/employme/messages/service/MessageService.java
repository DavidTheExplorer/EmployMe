package dte.employme.messages.service;

import static dte.employme.messages.Placeholders.NONE;

import org.bukkit.command.CommandSender;

import dte.employme.EmployMe;
import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;

@FunctionalInterface
public interface MessageService
{
	/**
	 * Retrieves a message by its {@code key}(unique identifier), and also replaces placeholders according to the provided {@code placeholders} object which holds their values.
	 * 
	 * @param key The key that identifies the message.
	 * @param placeholders The injected placeholders and their values.
	 * @return The final message.
	 */
	String getMessage(MessageKey key, Placeholders placeholders);
	
	default String getGeneralMessage(MessageKey key, Placeholders placeholders) 
	{
		return String.format("%s %s", EmployMe.CHAT_PREFIX, getMessage(key, placeholders));
	}
	
	
	
	/*
	 * Methods that send messages
	 */
	default void sendTo(CommandSender sender, MessageKey key, Placeholders placeholders) 
	{
		sender.sendMessage(getMessage(key, placeholders));
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey key, Placeholders placeholders) 
	{
		sender.sendMessage(getGeneralMessage(key, placeholders));
	}
	
	
	
	/*
	 * Delegation methods without placeholders
	 */
	default String getMessage(MessageKey key) 
	{
		return getMessage(key, NONE);
	}
	
	default String getGeneralMessage(MessageKey key)
	{
		return getGeneralMessage(key, NONE);
	}
	
	default void sendTo(CommandSender sender, MessageKey key) 
	{
		sendTo(sender, key, NONE);
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey key) 
	{
		sendGeneralMessage(sender, key, Placeholders.NONE);
	}
}