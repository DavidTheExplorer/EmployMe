package dte.employme.services.message;

import dte.employme.messages.MessageBuilder;
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
	MessageBuilder getMessage(MessageKey key);
}