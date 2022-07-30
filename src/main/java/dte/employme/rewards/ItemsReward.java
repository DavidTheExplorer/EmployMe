package dte.employme.rewards;

import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.inventories.PlayerContainerGUI;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;

@SerializableAs("Items Reward")
public class ItemsReward implements Reward, Iterable<ItemStack>
{
	private final Collection<ItemStack> items;
	private final PlayerContainerService playerContainerService;
	
	public ItemsReward(Collection<ItemStack> items, PlayerContainerService playerContainerService) 
	{
		this.items = items;
		this.playerContainerService = playerContainerService;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemsReward deserialize(Map<String, Object> serialized) 
	{
		List<ItemStack> items = (List<ItemStack>) serialized.get("Items");
		PlayerContainerService playerContainerService = ServiceLocator.getInstance(PlayerContainerService.class);
		
		return new ItemsReward(items, playerContainerService);
	}
	
	@Override
	public void giveTo(OfflinePlayer offlinePlayer)
	{
		PlayerContainerGUI rewardsContainer = this.playerContainerService.getRewardsContainer(offlinePlayer.getUniqueId());
		this.items.forEach(rewardsContainer::addItem);
	}
	
	public List<ItemStack> getItems() 
	{
		return Lists.newArrayList(this.items);
	}
	
	@Override
	public String getDescription() 
	{
		return this.items.stream()
				.map(ItemStackUtils::describe)
				.collect(joining(", "));
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