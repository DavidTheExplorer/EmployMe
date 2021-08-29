package com.pseudonova.employme.conversations;

import static com.pseudonova.employme.utils.ChatColorUtils.italic;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.inventory.ItemStack;

public class JobGoalPrompt extends RegexPrompt
{
	private final Prompt nextPrompt;
	
	public JobGoalPrompt(Prompt nextPrompt) 
	{
		super("[A-Za-z_]+:\\d+");
		
		this.nextPrompt = nextPrompt;
	}

	@Override
	public String getPromptText(ConversationContext context) 
	{
		return WHITE + "Which " + GREEN + "item" + WHITE + " do you need? Reply in this format: " + italic(AQUA) + "itemName:amount";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) 
	{
		String[] data = input.split(":");
		context.setSessionData("goal", new ItemStack(Material.matchMaterial(data[0]), Integer.valueOf(data[1])));
		
		return this.nextPrompt;
	}
	
	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		//check the format
		if(!super.isInputValid(context, input)) 
			return false;
		
		String materialName = input.split(":")[0];
		
		return Material.matchMaterial(materialName) != null;
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) 
	{
		return RED + "Invalid Format!";
	}
}
