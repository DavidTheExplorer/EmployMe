package dte.employme.utils;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.IntStream;

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
	 * Divides the provided {@code item} into smaller equivalent items, whose amounts are the max stack size. 
	 * The method will return a list of just the provided item if no division is needed.
	 * <p>
	 * Examples:
	 * <ul>
	 * 	<li>If the item is <b>40 snowballs</b> => the returned list is: <i>16 snowballs, 16 snowballs, 8 snowballs</i>.</li>
	 *  <li>If the item is <b>70 emeralds</b> => the returned list is: <i>64 emeralds, 6 emeralds</i>.</li>
	 * 	<li>If the item is <b>10 diamonds</b> => the returned list is just <i>10 diamonds</i>.</li>
	 * </ul>
	 * 
	 * @param item The item to break into corresponding smaller items.
	 * @return The smaller items.
	 */
	public static List<ItemStack> divide(ItemStack item)
	{
		int amount = item.getAmount();
		int maxAmount = item.getMaxStackSize();
		
		List<ItemStack> result = IntStream.rangeClosed(1, amount / maxAmount)
				.mapToObj(i -> changeAmount(item, maxAmount))
				.collect(toList());
		
		int leftover = amount % maxAmount;
		
		if(leftover > 0)
			result.add(changeAmount(item, leftover));
		
		return result;
	}
	
	private static ItemStack changeAmount(ItemStack item, int newAmount) 
	{
		ItemStack copy = item.clone();
		copy.setAmount(newAmount);

		return copy;
	}
}
