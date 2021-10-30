package dte.employme.messages;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GREEN;

import org.bukkit.command.CommandSender;

public interface MessageService
{
	String createMessage(MessageKey messageKey, Placeholders placeholders);
	String createGeneralMessage(MessageKey messageKey, Placeholders placeholders);
	
	default String createMessage(MessageKey messageKey) 
	{
		return createMessage(messageKey, Placeholders.NONE);
	}
	
	default String createGeneralMessage(MessageKey messageKey)
	{
		return createGeneralMessage(messageKey, Placeholders.NONE);
	}
	
	default void sendTo(CommandSender sender, MessageKey messageKey, Placeholders placeholders) 
	{
		sender.sendMessage(createMessage(messageKey, placeholders));
	}
	
	default void sendTo(CommandSender sender, MessageKey messageKey) 
	{
		sendTo(sender, messageKey, Placeholders.NONE);
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey messageKey, Placeholders placeholders) 
	{
		sender.sendMessage(createGeneralMessage(messageKey, placeholders));
	}
	
	default void sendGeneralMessage(CommandSender sender, MessageKey messageKey) 
	{
		sendGeneralMessage(sender, messageKey, Placeholders.NONE);
	}
	
	String PLUGIN_PREFIX = DARK_GREEN + "[" + GREEN + "EmployMe" + DARK_GREEN + "]";
}