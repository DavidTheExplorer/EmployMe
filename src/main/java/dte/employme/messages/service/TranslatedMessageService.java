package dte.employme.messages.service;

import java.util.Collection;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.reloadable.Reloadable;
import dte.employme.utils.java.EnumUtils;

public class TranslatedMessageService implements MessageService, Reloadable
{
	private final ConfigFile languageConfig;
	
	public TranslatedMessageService(ConfigFile languageConfig) 
	{
		this.languageConfig = languageConfig;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MessageBuilder getMessage(MessageKey key) 
	{
		//can be either a String or a List<String> - if the message contains multiple lines
		Object message = this.languageConfig.getConfig().get(String.format("Messages.%s", EnumUtils.fixEnumName(key)));
		
		if(message instanceof String) 
			return new MessageBuilder((String) message);
		
		if(isStringCollection(message))
			return new MessageBuilder(((Collection<String>) message).toArray(new String[0]));
					
		throw new IllegalArgumentException(String.format("The specified object(%s) doesn't represent a message!", message));
	}
	
	@Override
	public void reload() 
	{
		this.languageConfig.reload();
	}
	
	private static boolean isStringCollection(Object object) 
	{
		if(!(object instanceof Collection))
			return false;
		
		Collection<?> collection = (Collection<?>) object;
		
		return !collection.isEmpty() && collection.iterator().next() instanceof String;
	}
}