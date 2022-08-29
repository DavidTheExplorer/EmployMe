package dte.employme.config;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import dte.employme.messages.MessageKey;
import dte.employme.services.message.TranslatedMessageService;

public class ConfigFileFactory
{
	private final ExceptionHandler creationExceptionHandler, saveExceptionHandler;
	
	private boolean creationException, saveException;

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
			this.creationException = true;
			return null;
		}
	}
	
	public ConfigFile loadResource(String resourceName) 
	{
		return ConfigFile.loadResource(resourceName);
	}
	
	public ConfigFile loadMainConfig() 
	{
		return ConfigFile.loadResource("config");
	}
	
	public ConfigFile loadContainer(String subject) 
	{
		return loadConfig(String.format("containers/%s containers", subject));
	}
	
	public ConfigFile loadMessagesConfig(Messages defaultMessages) 
	{
		ConfigFile config = loadConfig("messages");
		
		if(config == null)
			return null;
		
		//regenerate the missing messages from the provided defaults
		Arrays.stream(MessageKey.VALUES)
		.filter(key -> !config.getConfig().contains(TranslatedMessageService.getConfigPath(key)))
		.forEach(missingKey -> 
		{
			String[] lines = defaultMessages.getLines(missingKey);
			Object message = lines.length == 1 ? lines[0] : Arrays.asList(lines);
			
			config.getConfig().addDefault(TranslatedMessageService.getConfigPath(missingKey), message);
		});
		
		config.getConfig().options().copyDefaults(true);
		
		return save(config) ? config : null;
	}
	
	public boolean anyCreationException() 
	{
		return this.creationException || this.saveException;
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
			this.saveException = true;
			return false;
		}
	}
	
	
	
	@FunctionalInterface
	public interface ExceptionHandler
	{
		void handle(IOException exception, ConfigFile config);
	}
	
	
	
	public static class Builder
	{
		ExceptionHandler creationExceptionHandler, saveExceptionHandler;
		
		@SafeVarargs
		public final Builder withSerializables(Class<? extends ConfigurationSerializable>... serializableClasses) 
		{
			Arrays.stream(serializableClasses).forEach(ConfigurationSerialization::registerClass);
			return this;
		}

		public Builder onCreationException(ExceptionHandler handler) 
		{
			this.creationExceptionHandler = handler;
			return this;
		}

		public Builder onSaveException(ExceptionHandler handler) 
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