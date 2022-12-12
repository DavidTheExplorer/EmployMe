package dte.employme.items.custom;

import static dte.employme.messages.MessageKey.CUSTOM_ITEM_NOT_FOUND;
import static dte.employme.messages.MessageKey.INVALID_CUSTOM_ITEM_FORMAT;

import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;

public class MMOItemsProvider extends CustomItemProvider
{
	private static final Pattern REQUEST_PATTERN = Pattern.compile(".+:.+");
	
	public MMOItemsProvider() 
	{
		super("type:item_name", request -> REQUEST_PATTERN.matcher(request).matches());
	}
	
	@Override
	protected void validateRequestFormat(String requestFormat)
	{
		super.validateRequestFormat(requestFormat);
		
		if(requestFormat.split(":").length != 2) 
			throw new CustomItemParseException(INVALID_CUSTOM_ITEM_FORMAT);
	}

	@Override
	public ItemStack parse(String requestFormat) 
	{
		validateRequestFormat(requestFormat);
		
		String[] data = requestFormat.split(":");
		
		Type type = MMOItems.plugin.getTypes().get(data[0].toUpperCase());
		
		if(type == null)
			throw new CustomItemParseException(CUSTOM_ITEM_NOT_FOUND);
		
		MMOItem mmoItem = MMOItems.plugin.getMMOItem(type, data[1].toUpperCase());
		
		if(mmoItem == null)
			throw new CustomItemParseException(CUSTOM_ITEM_NOT_FOUND);
		
		return mmoItem.newBuilder().build();
	}
}
