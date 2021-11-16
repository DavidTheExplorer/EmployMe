package dte.employme.job.prompts;

import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.inventory.ItemStack;

import dte.employme.messages.service.MessageService;
import dte.employme.utils.java.NumberUtils;

public class JobGoalPrompt extends RegexPrompt
{
	private final Prompt nextPrompt;
	private final MessageService messageService;
	
	public JobGoalPrompt(Prompt nextPrompt, MessageService messageService) 
	{
		super("[A-Za-z_]+:\\d+");
		
		this.nextPrompt = nextPrompt;
		this.messageService = messageService;
	}

	@Override
	public String getPromptText(ConversationContext context) 
	{
		return this.messageService.getMessage(ITEM_GOAL_FORMAT_QUESTION);
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
		if(!super.isInputValid(context, input))
			return false;
		
		Material material = Material.matchMaterial(input.split(":")[0]);
		
		if(material == null || material.isAir())
			return false;
		
		return NumberUtils.parseInt(input.split(":")[1]).isPresent();
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(ITEM_GOAL_INVALID);
	}
}
