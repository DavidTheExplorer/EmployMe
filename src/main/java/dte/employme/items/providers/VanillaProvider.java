package dte.employme.items.providers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.ItemStackUtils;

public class VanillaProvider extends ItemProvider
{
	public static final VanillaProvider INSTANCE = new VanillaProvider();
	
	public VanillaProvider()
	{
		super("Vanilla", "materialName");
	}

	@Override
	public boolean equals(ItemStack item1, ItemStack item2) 
	{
		return item1.isSimilar(item2);
	}
	
	@Override
	public boolean isAvailable() 
	{
		return true;
	}
	
	@Override
	public String getDisplayName(ItemStack providedItem) 
	{
		return ItemStackUtils.describe(providedItem);
	}

	@Override
	public ItemStack parse(String requestFormat) throws CustomItemParseException 
	{
		return new ItemStack(Material.matchMaterial(requestFormat));
	}
}
