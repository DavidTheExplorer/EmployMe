package dte.employme.messages.service;

import dte.employme.messages.MessageKey;

public class ForwardingMessageService implements MessageService
{
	protected final MessageService delegate;
	
	protected ForwardingMessageService(MessageService delegate) 
	{
		this.delegate = delegate;
	}
	
	@Override
	public String getMessage(MessageKey key) 
	{
		return this.delegate.getMessage(key);
	}
}
