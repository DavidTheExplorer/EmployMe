package dte.employme.items.custom;

import org.bukkit.inventory.ItemStack;

import dte.employme.items.custom.CustomItem.AbstractCustomItem;
import io.lumine.mythic.lib.api.item.NBTItem;

public class MMOItemsItem extends AbstractCustomItem
{
	private final String type, id;
	
	public MMOItemsItem(ItemStack item)
	{
		super(item);
		
		NBTItem nbtItem = NBTItem.get(item);
		this.type = nbtItem.getType();
		this.id = nbtItem.getString("MMOITEMS_ITEM_ID");
	}
	
	@Override
	public boolean equals(ItemStack item) 
	{
		NBTItem nbtItem = NBTItem.get(item);
		
		if(!nbtItem.hasType())
			return false;
		
		if(!nbtItem.getType().equals(this.type))
			return false;
		
		if(!nbtItem.getString("MMOITEMS_ITEM_ID").equals(this.id))
			return false;
		
		return true;
	}
}
