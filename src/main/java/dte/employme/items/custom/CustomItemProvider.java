package dte.employme.items.custom;

import static dte.employme.messages.MessageKey.INVALID_CUSTOM_ITEM_FORMAT;

import java.util.function.Predicate;

import org.bukkit.inventory.ItemStack;

public abstract class CustomItemProvider 
{
	private final String requestFormat;
	private final Predicate<String> requestFormatTester;
	
	protected CustomItemProvider(String requestFormat, Predicate<String> requestFormatTester) 
	{
		this.requestFormat = requestFormat;
		this.requestFormatTester = requestFormatTester;
	}
	
	public String getRequestFormat() 
	{
		return this.requestFormat;
	}
	
	protected void validateRequestFormat(String requestFormat) 
	{
		if(!this.requestFormatTester.test(requestFormat))
			throw new CustomItemParseException(INVALID_CUSTOM_ITEM_FORMAT);
	}
	
	public abstract ItemStack parse(String requestFormat) throws CustomItemParseException;
}
