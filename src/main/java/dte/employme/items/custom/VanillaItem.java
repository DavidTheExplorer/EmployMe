package dte.employme.items.custom;

import org.bukkit.inventory.ItemStack;

import dte.employme.items.custom.CustomItem.AbstractCustomItem;

public class VanillaItem extends AbstractCustomItem
{
	public VanillaItem(ItemStack item)
	{
		super(item);
	}

	@Override
	public boolean equals(ItemStack item) 
	{
		return this.item.equals(item);
	}
}
