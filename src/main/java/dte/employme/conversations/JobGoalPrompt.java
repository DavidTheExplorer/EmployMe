package dte.employme.conversations;

import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import dte.employme.services.message.MessageService;

public class JobGoalPrompt extends ValidatingPrompt
{
	private final MessageService messageService;
	
	public JobGoalPrompt(MessageService messageService) 
	{
		this.messageService = messageService;
	}

	@Override
	public String getPromptText(ConversationContext context)
	{
		return this.messageService.getMessage(ITEM_GOAL_FORMAT_QUESTION).first();
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		Material material = Material.matchMaterial(input);
		
		if(material == null)
			return false;
		
		return !material.isAir() && material != Material.BARRIER;
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) 
	{
		context.setSessionData("material", Material.matchMaterial(input));
		
		return Prompt.END_OF_CONVERSATION;
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(ITEM_GOAL_INVALID).first();
	}
}