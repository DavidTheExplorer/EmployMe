package dte.employme.conversations;

import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import dte.employme.services.message.MessageService;
import dte.employme.utils.MaterialUtils;

public class JobGoalPrompt extends ValidatingPrompt
{
	private final MessageService messageService;
	private final String question;
	
	public JobGoalPrompt(MessageService messageService, String question) 
	{
		this.messageService = messageService;
		this.question = question;
	}

	@Override
	public String getPromptText(ConversationContext context)
	{
		return this.question;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		Material material = Material.matchMaterial(input);
		
		return Optional.ofNullable(material)
				.filter(MaterialUtils::isObtainable)
				.isPresent();
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