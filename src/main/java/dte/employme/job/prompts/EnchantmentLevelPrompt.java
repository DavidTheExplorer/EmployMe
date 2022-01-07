package dte.employme.job.prompts;

import static dte.employme.messages.MessageKey.ENCHANTMENT_LEVEL_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.ENCHANTMENT_LEVEL_OUT_OF_BOUNDS;
import static dte.employme.messages.MessageKey.ENTER_ENCHANTMENT_LEVEL;
import static dte.employme.messages.Placeholders.ENCHANTMENT;
import static dte.employme.messages.Placeholders.ENCHANTMENT_MAX_LEVEL;
import static dte.employme.messages.Placeholders.ENCHANTMENT_MIN_LEVEL;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;

import dte.employme.messages.service.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.java.NumberUtils;

public class EnchantmentLevelPrompt extends NumericPrompt
{
	private final Enchantment enchantment;
	private final MessageService messageService;

	public EnchantmentLevelPrompt(Enchantment enchantment, MessageService messageService) 
	{
		this.enchantment = enchantment;
		this.messageService = messageService;
	}

	@Override
	public String getPromptText(ConversationContext context) 
	{
		return this.messageService.getMessage(ENTER_ENCHANTMENT_LEVEL)
				.inject(ENCHANTMENT, EnchantmentUtils.getDisplayName(this.enchantment))
				.first();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) 
	{
		context.setSessionData("level", input.intValue());
		
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		int level = input.intValue();

		return level >= this.enchantment.getStartLevel() && level <= this.enchantment.getMaxLevel();
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) 
	{
		return super.isInputValid(context, input) && NumberUtils.parseInt(input).isPresent();
	}

	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		return this.messageService.getMessage(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS)
				.inject(ENCHANTMENT_MIN_LEVEL, String.valueOf(this.enchantment.getStartLevel()))
				.inject(ENCHANTMENT_MAX_LEVEL, String.valueOf(this.enchantment.getMaxLevel()))
				.first();
	}

	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(ENCHANTMENT_LEVEL_NOT_A_NUMBER).first();
	}
}