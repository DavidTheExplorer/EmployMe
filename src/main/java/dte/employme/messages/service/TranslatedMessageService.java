package dte.employme.messages.service;

import java.util.Collection;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.utils.java.EnumUtils;

public class TranslatedMessageService implements MessageService
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
		Object message = this.languageConfig.getConfig().get(String.format("Messages.%s", EnumUtils.fixEnumName(key)));
		
		if(message instanceof String) 
			return new MessageBuilder((String) message);
		
		if(isStringCollection(message))
			return new MessageBuilder(((Collection<String>) message).toArray(new String[0]));
					
		throw new IllegalArgumentException("The specified object doesn't represent a message!");
	}
	
	private static boolean isStringCollection(Object object) 
	{
		if(!(object instanceof Collection))
			return false;
		
		Collection<?> collection = (Collection<?>) object;
		
		if(collection.isEmpty())
			return false;
		
		if(!(collection.iterator().next() instanceof String))
			return false;
		
		return true;
	}
}