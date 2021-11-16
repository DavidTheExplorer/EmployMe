package dte.employme.messages.service;

import static dte.employme.utils.ChatColorUtils.colorize;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;
import dte.employme.utils.java.EnumUtils;

public class TranslatedMessageService implements MessageService
{
	private final ConfigFile languageConfig;
	
	public TranslatedMessageService(ConfigFile languageConfig) 
	{
		this.languageConfig = languageConfig;
	}
	
	@Override
	public String getMessage(MessageKey key, Placeholders placeholders) 
	{
		String finalMessage;
		
		finalMessage = this.languageConfig.getConfig().getString(getConfigPath(key));
		finalMessage = colorize(finalMessage);
		finalMessage = placeholders.apply(finalMessage);
		
		return finalMessage;
	}
	
	private static String getConfigPath(MessageKey key) 
	{
		return String.format("Messages.%s", EnumUtils.fixEnumName(key));
	}
}