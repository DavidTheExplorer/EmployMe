package dte.employme.utils.items.builder;

import org.bukkit.ChatColor;

public enum ClickableMode
{
	LEFT("Left Click"),
	RIGHT("Right Click"),
	LEFT_FIRST("Left Click / Right Click"),
	RIGHT_FIRST("Right Click / Left Click");

	private final String suffix;

	ClickableMode(String suffix)
	{
		this.suffix = suffix;
	}
	
	public String getSuffix(ChatColor mouseButtonsColor, ChatColor slashColor)
	{
		String colored = this.suffix;

		if(colored.contains("Left")) 
		{
			colored = colored.replace("Left", mouseButtonsColor + "Left");
		}
		if(colored.contains("Right")) 
		{
			colored = colored.replace("Right", mouseButtonsColor + "Right");
		}
		colored = colored.replace("/", slashColor + "/");
		
		return colored;
	}
}
