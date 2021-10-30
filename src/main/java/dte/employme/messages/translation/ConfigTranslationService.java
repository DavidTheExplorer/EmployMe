package dte.employme.messages.translation;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageKey;
import dte.employme.utils.java.EnumUtils;

public class ConfigTranslationService extends AbstractTranslationService
{
	private final Map<MessageKey, String> configNames;
	
	private static final Map<MessageKey, String> DEFAULT_CONFIG_NAMES = Arrays.stream(MessageKey.values())
			.collect(toMap(Function.identity(), EnumUtils::fixEnumName));
	
	public ConfigTranslationService(String defaultLanguage)
	{
		super(defaultLanguage);
		
		this.configNames = new HashMap<>(DEFAULT_CONFIG_NAMES);
	}

	@Override
	public String translate(MessageKey messageKey, String targetLanguage) 
	{
		ConfigFile languageConfig = ConfigFile.byPath(String.format("languages/%s.yml", targetLanguage));
		
		if(!languageConfig.exists())
			throw new IllegalStateException(String.format("The messages file for language '%s' wasn't found!", this.defaultLanguage));
		
		return languageConfig.getConfig().getString(getConfigPath(messageKey));
	}
	
	public String getConfigPath(MessageKey messageKey) 
	{
		return String.format("Messages.%s", this.configNames.get(messageKey));
	}
	
	public void setConfigName(MessageKey messageKey, String newID) 
	{
		this.configNames.put(messageKey, newID);
	}
}