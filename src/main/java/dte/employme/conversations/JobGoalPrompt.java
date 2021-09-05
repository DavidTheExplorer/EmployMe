package dte.employme.conversations;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.inventory.ItemStack;

import dte.employme.messages.Message;

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
		return Message.ITEM_GOAL_FORMAT_QUESTION.toString();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) 
	{
		String[] materialAndAmount = input.split(":");
		context.setSessionData("goal", new ItemStack(Material.matchMaterial(materialAndAmount[0]), Integer.valueOf(materialAndAmount[1])));
		
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
		return Message.ITEM_GOAL_INVALID_FORMAT.toString();
	}
}
