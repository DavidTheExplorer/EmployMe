package dte.employme.items.custom;

import dte.employme.messages.MessageKey;

public class CustomItemParseException extends RuntimeException
{
	private final MessageKey errorMessage;
	
	private static final long serialVersionUID = 7490336701240557150L;
	
	public CustomItemParseException(MessageKey errorMessage) 
	{
		this.errorMessage = errorMessage;
	}
	
	public MessageKey getErrorMessage() 
	{
		return this.errorMessage;
	}
}