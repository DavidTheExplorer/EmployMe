package dte.employme.job.rewards;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import dte.employme.job.service.JobService;
import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;
import dte.employme.visitors.reward.RewardVisitor;

@SerializableAs("Items Reward")
public class ItemsReward implements Reward
{
	private final Iterable<ItemStack> items;
	private final JobService jobService;
	
	public ItemsReward(Iterable<ItemStack> items, JobService jobService) 
	{
		this.items = items;
		this.jobService = jobService;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemsReward deserialize(Map<String, Object> serialized) 
	{
		List<ItemStack> items = (List<ItemStack>) serialized.get("Items");
		JobService jobService = ServiceLocator.getInstance(JobService.class);
		
		return new ItemsReward(items, jobService);
	}
	
	@Override
	public void giveTo(Player player)
	{
		Inventory rewardsContainer = this.jobService.getRewardsContainer(player.getUniqueId());
		
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