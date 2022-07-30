package dte.employme.utils;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.STRIKETHROUGH;
import static org.bukkit.ChatColor.UNDERLINE;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

public class ChatColorUtils
{
	public static String colorize(String text) 
	{
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String createSeparationLine(ChatColor color, int length) 
	{
		return strikeThrough(color) + StringUtils.repeat("-", length);
	}
	
	public static ChatColor searchFirstColor(String text) 
	{
		int charColorIndex = text.indexOf(ChatColor.COLOR_CHAR);

		if(charColorIndex == -1) 
			return null;

		return ChatColor.getByChar(text.charAt(charColorIndex+1));
	}
	
	
	/*
	 * Color Formatting
	 */
	public static String bold(ChatColor color) 
	{
		return color + BOLD.toString();
	}

	public static String underlined(ChatColor color) 
	{
		return color + UNDERLINE.toString();
	}

	public static String italic(ChatColor color) 
	{
		return color + ITALIC.toString();
	}

	public static String strikeThrough(ChatColor color) 
	{
		return color + STRIKETHROUGH.toString();
	}
}