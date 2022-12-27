package dte.employme.items.providers;

import static dte.employme.messages.MessageKey.CUSTOM_ITEM_NOT_FOUND;
import static dte.employme.messages.MessageKey.INVALID_CUSTOM_ITEM_FORMAT;

import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.java.Pluraliser;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;

public class MMOItemsProvider extends ItemProvider
{
	public static final MMOItemsProvider INSTANCE = new MMOItemsProvider();
	
	private static final Pattern REQUEST_PATTERN = Pattern.compile(".+:.+");
	
	public MMOItemsProvider()
	{
		super("MMOItems", "type:item_name");
	}
	
	@Override
	public ItemStack parse(String requestFormat) 
	{
		if(!REQUEST_PATTERN.matcher(requestFormat).matches()) 
			throw new CustomItemParseException(INVALID_CUSTOM_ITEM_FORMAT);
		
		String[] data = requestFormat.split(":");
		
		Type type = MMOItems.plugin.getTypes().get(data[0].toUpperCase());
		
		if(type == null)
			throw new CustomItemParseException(CUSTOM_ITEM_NOT_FOUND);
		
		MMOItem mmoItem = MMOItems.plugin.getMMOItem(type, data[1].toUpperCase());
		
		if(mmoItem == null)
			throw new CustomItemParseException(CUSTOM_ITEM_NOT_FOUND);
		
		return mmoItem.newBuilder().build();
	}
	
	@Override
	public boolean equals(ItemStack item1, ItemStack item2) 
	{
		NBTItem item1NBT = NBTItem.get(item1);

		if(!item1NBT.hasType())
			throw new IllegalArgumentException("Unable to check whether an MMOItems item equals to another item because the former is not from MMOItems!");

		NBTItem item2NBT = NBTItem.get(item2);

		if(!item2NBT.hasType())
			return false;

		if(!item1NBT.getType().equals(item2NBT.getType()))
			return false;

		if(!item1NBT.getString("MMOITEMS_ITEM_ID").equals(item2NBT.getString("MMOITEMS_ITEM_ID")))
			return false;

		return true;
	}
	
	@Override
	public String getDisplayName(ItemStack providedItem) 
	{
		String itemName = WordUtils.capitalizeFully(NBTItem.get(providedItem).getString("MMOITEMS_ITEM_ID").replace('_', ' '));
		
		return Pluraliser.pluralise(itemName, providedItem.getAmount());
	}
}