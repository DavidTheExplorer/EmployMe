package dte.employme.messages.service;

import dte.employme.EmployMe;
import dte.employme.messages.MessageKey;

@FunctionalInterface
public interface MessageService
{
	/**
	 * Retrieves the message that corresponds to the provided {@code key} - which is its unique identifier.
	 * 
	 * @param key The key that identifies the message.
	 * @return The message.
	 */
	String getMessage(MessageKey key);
	
	/**
	 * Retrieves the message that corresponds to the provided {@code key}, prefixed with the EmployMe's chat prefix.
	 * @param key The key that identifies the message.
	 * 
	 * @return The message.
	 */
	default String getGeneralMessage(MessageKey key) 
	{
		return String.format("%s %s", EmployMe.CHAT_PREFIX, getMessage(key));
	}
}