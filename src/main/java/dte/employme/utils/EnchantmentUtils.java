package dte.employme.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentUtils
{
	//Container of static methods
	private EnchantmentUtils(){}
	
	public static boolean isEnchantable(Material material) 
	{
		return isEnchantable(new ItemStack(material));
	}
	
	public static boolean isEnchantable(ItemStack item)
	{
		return Arrays.stream(Enchantment.values()).anyMatch(enchantment -> enchantment.canEnchantItem(item));
	}
}