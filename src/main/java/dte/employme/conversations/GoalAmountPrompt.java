package dte.employme.conversations;

import static dte.employme.messages.MessageKey.CONVERSATION_ESCAPE_TITLE;
import static dte.employme.messages.MessageKey.CONVERSATION_ESCAPE_WORD;
import static dte.employme.messages.MessageKey.GOAL_AMOUNT_MUST_BE_POSITIVE;
import static dte.employme.messages.MessageKey.GOAL_AMOUNT_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.GOAL_AMOUNT_QUESTION;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import dte.employme.services.message.MessageService;
import dte.employme.utils.java.NumberUtils;

public class GoalAmountPrompt extends NumericPrompt
{
	private final MessageService messageService;

	public GoalAmountPrompt(MessageService messageService) 
	{
		this.messageService = messageService;
	}

	@Override
	public String getPromptText(ConversationContext context) 
	{
		Player player = (Player) context.getForWhom();

		//send the escape hint title
		this.messageService.getMessage(CONVERSATION_ESCAPE_TITLE)
		.inject("escape word", this.messageService.getMessage(CONVERSATION_ESCAPE_WORD).first())
		.sendTitleTo(player);

		return this.messageService.getMessage(GOAL_AMOUNT_QUESTION).first();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) 
	{
		context.setSessionData("amount", input.intValue());
		
		return END_OF_CONVERSATION;
	}

	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		return input.intValue() > 0;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) 
	{
		return super.isInputValid(context, input) && NumberUtils.parseInt(input).isPresent();
	}

	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		return this.messageService.getMessage(GOAL_AMOUNT_MUST_BE_POSITIVE).first();
	}

	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(GOAL_AMOUNT_NOT_A_NUMBER).first();
	}
}
