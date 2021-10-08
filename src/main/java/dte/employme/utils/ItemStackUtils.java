package dte.employme.utils;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.java.EnumUtils;
import dte.employme.utils.java.MapBuilder;

public class ItemStackUtils
{
	//Container of static methods
	private ItemStackUtils(){}
	
	private static final Map<String, String> PLURAL_SUFFIXES = new MapBuilder<String, String>()
			.put("x", "es")
			.put("z", "es")
			.put("sh", "es")
			.put("ch", "es")
			.build();

	public static String describe(ItemStack item) 
	{
		return describe(item.getType(), item.getAmount());
	}

	public static String describe(Material material, int amount)
	{
		String materialName = EnumUtils.fixEnumName(material);

		if(amount > 1) 
			materialName += getPluralSuffix(materialName);

		return String.format("%d %s", amount, materialName);
	}

	private static String getPluralSuffix(String word)
	{
		String lowerWord = word.toLowerCase();
		
		//if the word ends with a digit OR it's already plural - it doesn't need a suffix
		if(lowerWord.matches(".+\\d") || lowerWord.endsWith("s"))
			return "";
		
		return PLURAL_SUFFIXES.keySet().stream()
				.filter(lowerWord::endsWith)
				.findFirst()
				.map(PLURAL_SUFFIXES::get)
				.orElse("s");
	}
}
