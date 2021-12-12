package dte.employme.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.java.EnumUtils;
import dte.employme.utils.java.Pluraliser;

public class ItemStackUtils
{
	//Container of static methods
	private ItemStackUtils(){}

	public static String describe(ItemStack item) 
	{
		return describe(item.getType(), item.getAmount());
	}

	public static String describe(Material material, int amount)
	{
		String name = (amount == 1) ? EnumUtils.fixEnumName(material) : WordUtils.capitalizeFully(Pluraliser.pluralise(material.name().toLowerCase().replace('_', ' ')));

		return String.format("%d %s", amount, name);
	}
}
