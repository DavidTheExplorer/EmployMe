package dte.employme.utils;

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
}
