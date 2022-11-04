package dte.employme.configs;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import dte.employme.messages.MessageKey;
import dte.employme.messages.MessageProvider;
import dte.employme.services.message.TranslatedMessageService;
import dte.spigotconfiguration.SpigotConfig;
import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class MessagesConfig extends SpigotConfig
{
	public MessagesConfig(Plugin plugin, MessageProvider defaultMessages) throws ConfigLoadException
	{
		super(new Builder(plugin)
				.byPath("messages")
				.supplyDefaults(config -> 
				{
					//regenerate the missing messages based on the provided defaults
					return Arrays.stream(MessageKey.VALUES)
							.filter(key -> isMissing(config, key))
							.collect(toMap(TranslatedMessageService::getConfigPath, missingKey -> getDefaultMessage(missingKey, defaultMessages)));
				})
				.loadDefaults());
	}

	private static boolean isMissing(YamlConfiguration config, MessageKey key) 
	{
		return !config.contains(TranslatedMessageService.getConfigPath(key));
	}

	private static Object getDefaultMessage(MessageKey missingKey, MessageProvider defaultMessages) 
	{
		String[] lines = defaultMessages.provide(missingKey);

		return lines.length == 1 ? lines[0] : Arrays.asList(lines);
	}
  
}
