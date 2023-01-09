package dte.employme.items.providers;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public abstract class ItemProvider
{
	private final String name;
	private final String requestFormat;
	
	protected ItemProvider(String name, String requestFormat) 
	{
		this.name = name;
		this.requestFormat = requestFormat;
	}
	
	public String getName() 
	{
		return this.name;
	}
	
	public String getRequestFormat() 
	{
		return this.requestFormat;
	}
	
	public boolean isAvailable() 
	{
		return Bukkit.getPluginManager().isPluginEnabled(this.name);
	}
	
	public static Set<ItemProvider> getAvailable()
	{
		return Stream.of(new MMOItemsProvider())
				.filter(ItemProvider::isAvailable)
				.collect(toSet());
	}
	
	/**
	 * Returns an item that is uniquely recognized by this provider, according to the provided {@code request}.
	 * 
	 * @param request The id of the item to return.
	 * @return The requested item.
	 * @throws CustomItemParseException when the request format cannot be parsed.
	 */
	public abstract ItemStack parse(String request) throws CustomItemParseException;
	
	/**
	 * Returns whether the first item(must be from this provider) equals to the second one, which may not be from this provider.
	 * 
	 * @param item1 The item from this provider to be compared with another item.
	 * @param item2 The item to compare to.
	 * @return Whether both items represent the same item.
	 */
	public abstract boolean equals(ItemStack item1, ItemStack item2);
	
	/**
	 * Returns an elegant display name for an item which was previously provided.
	 * 
	 * @param providedItem The item to get a display name for.
	 * @return A display name for the item.
	 */
	public abstract String getDisplayName(ItemStack providedItem);
}
