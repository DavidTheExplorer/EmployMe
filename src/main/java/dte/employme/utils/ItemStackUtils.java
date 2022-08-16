package dte.employme.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.java.EnumUtils;
import dte.employme.utils.java.Pluraliser;
import dte.employme.utils.java.StringUtils;

public class ItemStackUtils
{
	public static String describe(ItemStack item) 
	{
		return describe(item.getType(), item.getAmount());
	}

	public static String describe(Material material, int amount)
	{
		String name = (amount == 1) ? EnumUtils.fixEnumName(material) : StringUtils.capitalizeFully(Pluraliser.pluralise(material.name().toLowerCase().replace('_', ' ')));

		return String.format("%d %s", amount, name);
	}
	
	/**
	 * If the provided {@code item}'s amount <= its stack size, this method returns a list of it.
	 * Otherwise, it breaks it into smaller equivalent items whose amounts are the stack size.
	 * <p>
	 * For example, if the item is <b>40 snowballs</b>, the returned list would be: <i>16 snowballs, 16 snowballs, 8 snowballs</i>.
	 * 
	 * @param item The item to break into corresponding smaller items.
	 * @return The smaller items.
	 */
	public static List<ItemStack> divideBigItem(ItemStack item)
	{
		int sizeLeft = item.getAmount();
		int maxSize = item.getType().getMaxStackSize();
		
		List<ItemStack> items = new ArrayList<>();
		
		while(sizeLeft > 0)
		{
			ItemStack newItem = item.clone();
			int sizeToRemove = sizeLeft > maxSize ? maxSize : sizeLeft;
			
			newItem.setAmount(sizeToRemove);
			sizeLeft -= sizeToRemove;
			items.add(newItem);
		}
		
		return items;
	}
}
