package dte.employme.job.rewards;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.containers.PlayerContainer;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;
import dte.employme.visitors.reward.RewardVisitor;

@SerializableAs("Items Reward")
public class ItemsReward implements Reward
{
	private final Iterable<ItemStack> items;
	private final PlayerContainerService playerContainerService;
	
	public ItemsReward(Iterable<ItemStack> items, PlayerContainerService playerContainerService) 
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
	public void giveTo(Player player)
	{
		PlayerContainer rewardsContainer = this.playerContainerService.getRewardsContainer(player.getUniqueId());
		
		this.items.forEach(rewardsContainer::addItem);
	}
	
	public List<ItemStack> getItems() 
	{
		return Lists.newArrayList(this.items);
	}

	@Override
	public <R> R accept(RewardVisitor<R> visitor) 
	{
		return visitor.visit(this);
	}

	@Override
	public Map<String, Object> serialize() 
	{
		return new MapBuilder<String, Object>()
				.put("Items", this.items)
				.build();
	}
}