package dte.employme.messages.service;

import static dte.employme.utils.ChatColorUtils.colorize;

import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;
import dte.employme.messages.translation.TranslationService;

public class TranslatedMessageService implements MessageService
{
	private final TranslationService translationService;

	public TranslatedMessageService(TranslationService translationService) 
	{
		this.translationService = translationService;
	}

	@Override
	public String getMessage(MessageKey key, Placeholders placeholders) 
	{
		String finalMessage;
		
		finalMessage = this.translationService.translate(key);
		finalMessage = colorize(finalMessage);
		finalMessage = placeholders.apply(finalMessage);
		
		return finalMessage;
	}
}