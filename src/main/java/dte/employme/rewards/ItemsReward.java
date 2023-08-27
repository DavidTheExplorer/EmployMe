package dte.employme.rewards;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;
import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;

@SerializableAs("Items Reward")
public class ItemsReward implements Reward, Iterable<ItemStack>
{
	private final Collection<ItemStack> items;
	
	public ItemsReward(Collection<ItemStack> items) 
	{
		this.items = items;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemsReward deserialize(Map<String, Object> serialized) 
	{
		return new ItemsReward((List<ItemStack>) serialized.get("Items"));
	}
	
	@Override
	public void giveTo(OfflinePlayer offlinePlayer)
	{
		ItemPaletteGUI rewardsContainer = ServiceLocator.getInstance(PlayerContainerService.class).getRewardsContainer(offlinePlayer.getUniqueId());
		this.items.forEach(rewardsContainer::addItem);
	}
	
	public List<ItemStack> getItems() 
	{
		return Lists.newArrayList(this.items);
	}

	@Override
	public Map<String, Object> serialize() 
	{
		return new MapBuilder<String, Object>()
				.put("Items", this.items)
				.build();
	}
	
	@Override
	public Iterator<ItemStack> iterator() 
	{
		return this.items.iterator();
	}

	@Override
	public String toString()
	{
		return String.format("ItemsReward [items=%s]", this.items);
	}
}