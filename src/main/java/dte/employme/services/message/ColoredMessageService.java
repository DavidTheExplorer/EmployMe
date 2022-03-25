package dte.employme.services.message;

import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.utils.ChatColorUtils;

public class ColoredMessageService extends ForwardingMessageService
{
	public ColoredMessageService(MessageService delegate)
	{
		super(delegate);
	}
	
	@Override
	public MessageBuilder getMessage(MessageKey key) 
	{
		return super.getMessage(key).map(ChatColorUtils::colorize);
	}
}