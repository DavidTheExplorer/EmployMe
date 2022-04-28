package dte.employme.config;

import java.io.IOException;
import java.util.Arrays;

import dte.employme.messages.MessageKey;
import dte.employme.utils.java.EnumUtils;

public class ConfigFileFactory
{
	private final ExceptionHandler creationExceptionHandler, saveExceptionHandler;

	private ConfigFileFactory(Builder builder) 
	{
		this.creationExceptionHandler = builder.creationExceptionHandler;
		this.saveExceptionHandler = builder.saveExceptionHandler;
	}
	
	public ConfigFile loadConfig(String path) 
	{
		ConfigFile config = ConfigFile.byPath(path);
		
		try 
		{
			ConfigFile.createIfAbsent(config);
			return config;
		}
		catch(IOException exception) 
		{
			this.creationExceptionHandler.handle(exception, config);
			return null;
		}
	}
	
	public ConfigFile loadContainer(String subject) 
	{
		return loadConfig(String.format("containers/%s containers", subject));
	}
	
	public ConfigFile loadMessagesConfig(Messages defaultProvider) 
	{
		ConfigFile config = loadConfig("messages");
		
		if(config == null)
			return null;
		
		regenerateMissingMessages(config, defaultProvider);
		
		return save(config) ? config : null;
	}


	private boolean save(ConfigFile config) 
	{
		try 
		{
			config.save();
			return true;
		} 
		catch(IOException exception)
		{
			this.saveExceptionHandler.handle(exception, config);
			return false;
		}
	}
	
	private static void regenerateMissingMessages(ConfigFile languageConfig, Messages defaultsProvider) 
	{
		Arrays.stream(MessageKey.VALUES)
		.filter(key -> !languageConfig.getConfig().contains(getPath(key)))
		.forEach(missingKey -> 
		{
			String[] lines = defaultsProvider.getLines(missingKey);
			Object message = lines.length == 1 ? lines[0] : Arrays.asList(lines);
			
			languageConfig.getConfig().addDefault(getPath(missingKey), message);
		});
		
		languageConfig.getConfig().options().copyDefaults(true);
	}
	
	private static String getPath(MessageKey messageKey) 
	{
		return "Messages." + EnumUtils.fixEnumName(messageKey);
	}
	
	
	
	@FunctionalInterface
	public interface ExceptionHandler
	{
		void handle(IOException exception, ConfigFile config);
	}
	
	
	
	public static class Builder
	{
		ExceptionHandler creationExceptionHandler, saveExceptionHandler;

		public Builder handleCreationException(ExceptionHandler handler) 
		{
			this.creationExceptionHandler = handler;
			return this;
		}

		public Builder handleSaveException(ExceptionHandler handler) 
		{
			this.saveExceptionHandler = handler;
			return this;
		}

		public ConfigFileFactory build() 
		{
			return new ConfigFileFactory(this);
		}
	}
}