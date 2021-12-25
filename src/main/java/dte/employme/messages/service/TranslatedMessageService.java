package dte.employme.messages.service;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageKey;
import dte.employme.utils.java.EnumUtils;

public class TranslatedMessageService implements MessageService
{
	private final ConfigFile languageConfig;
	
	public TranslatedMessageService(ConfigFile languageConfig) 
	{
		this.languageConfig = languageConfig;
	}
	
	@Override
	public String getMessage(MessageKey key) 
	{
		String configPath = String.format("Messages.%s", EnumUtils.fixEnumName(key));
		
		return this.languageConfig.getConfig().getString(configPath);
	}
}