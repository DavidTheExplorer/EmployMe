package dte.employme.messages.service;

import static dte.employme.utils.ChatColorUtils.colorize;

import dte.employme.messages.MessageKey;

public class ColoredMessageService extends ForwardingMessageService
{
	public ColoredMessageService(MessageService delegate)
	{
		super(delegate);
	}
	
	@Override
	public String getMessage(MessageKey key) 
	{
		return colorize(super.getMessage(key));
	}
}
