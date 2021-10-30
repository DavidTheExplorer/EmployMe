package dte.employme.messages;

import static dte.employme.utils.ChatColorUtils.colorize;

import dte.employme.messages.translation.TranslationService;

public class TranslatedMessageService implements MessageService
{
	private final TranslationService translationService;

	public TranslatedMessageService(TranslationService translationService) 
	{
		this.translationService = translationService;
	}

	@Override
	public String createMessage(MessageKey messageKey, Placeholders placeholders) 
	{
		String finalMessage;
		
		finalMessage = this.translationService.translate(messageKey);
		finalMessage = colorize(finalMessage);
		finalMessage = placeholders.apply(finalMessage);
		
		return finalMessage;
	}

	@Override
	public String createGeneralMessage(MessageKey messageKey, Placeholders placeholders) 
	{
		return String.format("%s %s", PLUGIN_PREFIX, createMessage(messageKey, placeholders));
	}
}
