package dte.employme.utils;

import static dte.employme.utils.java.Predicates.negate;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import dte.employme.utils.java.EnumUtils;

public class EnchantmentUtils
{
	//Container of static methods
	private EnchantmentUtils(){}

	public static String getDisplayName(Enchantment enchantment) 
	{
		return EnumUtils.fixEnumName(enchantment.getKey().getKey());
	}



	/*
	 * Replacement methods to enchantment related ones that treat Enchantment Books' Stored Enchantments as regular enchantments.
	 */
	public static boolean isEnchantable(Material material) 
	{
		return isEnchantable(new ItemStack(material));
	}

	public static boolean isEnchantable(ItemStack item)
	{
		if(isEnchantedBook(item))
			return true;

		return Arrays.stream(Enchantment.values()).anyMatch(enchantment -> enchantment.canEnchantItem(item));
	}

	public static void enchant(ItemStack item, Enchantment enchantment, int level) 
	{
		if(isEnchantedBook(item))
			ifEnchantedBook(item, meta -> meta.addStoredEnchant(enchantment, level, true));
		else
			item.addUnsafeEnchantment(enchantment, level);
	}

	public static void removeEnchantment(ItemStack item, Enchantment enchantment) 
	{
		ifEnchantedBook(item, meta -> meta.removeStoredEnchant(enchantment));
		item.removeEnchantment(enchantment);
	}

	public static boolean canEnchantItem(Enchantment enchantment, ItemStack item) 
	{
		return isEnchantedBook(item) ? true : enchantment.canEnchantItem(item);
	}

	public static Map<Enchantment, Integer> getEnchantments(ItemStack item)
	{
		return !isEnchantedBook(item) ? item.getEnchantments() : ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants();
	}
	
	public static Map<Enchantment, Integer> getAllEnchantments(ItemStack item)
	{
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.putAll(item.getEnchantments());
		ifEnchantedBook(item, meta -> enchantments.putAll(meta.getStoredEnchants()));
		
		return enchantments;
	}

	public static Set<Enchantment> getRemainingEnchantments(ItemStack item)
	{
		Set<Enchantment> enchantments = getEnchantments(item).keySet();

		return Arrays.stream(Enchantment.values())
				.filter(negate(enchantments::contains))
				.filter(enchantment -> canEnchantItem(enchantment, item))
				.collect(toSet());
	}

	public static void ifEnchantedBook(ItemStack item, Consumer<EnchantmentStorageMeta> metaConsumer) 
	{
		if(!isEnchantedBook(item))
			return;

		EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) item.getItemMeta();
		metaConsumer.accept(itemMeta);
		item.setItemMeta(itemMeta);
	}

	private static boolean isEnchantedBook(ItemStack item) 
	{
		//TODO: replace with return item.getType() == Material.ENCHANTED_BOOK;
		return item.getItemMeta() instanceof EnchantmentStorageMeta;
	}
}