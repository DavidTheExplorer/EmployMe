package dte.employme.job;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import dte.employme.rewards.Reward;
import dte.employme.utils.java.MapBuilder;

@SerializableAs("Job")
public class Job implements ConfigurationSerializable
{
	private final OfflinePlayer employer;
	private final ItemStack goal;
	private final Reward reward;

	public Job(OfflinePlayer employer, ItemStack goal, Reward reward) 
	{
		this.employer = employer;
		this.goal = goal;
		this.reward = reward;
	}

	public Job(Map<String, Object> serialized) 
	{
		this(
				Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("Employer UUID"))), 
				(ItemStack) serialized.get("Goal"), 
				(Reward) serialized.get("Reward")
				);
	}

	public OfflinePlayer getEmployer() 
	{
		return this.employer;
	}

	public ItemStack getGoal() 
	{
		return new ItemStack(this.goal);
	}

	public Reward getReward() 
	{
		return this.reward;
	}

	@Override
	public Map<String, Object> serialize()
	{
		return new MapBuilder<String, Object>()
				.put("Employer UUID", this.employer.getUniqueId().toString())
				.put("Goal", this.goal)
				.put("Reward", this.reward)
				.build();
	}

	@Override
	public String toString() 
	{
		return String.format("Job [employer=%s, goal=%s, reward=%s]", this.employer.getUniqueId().toString(), this.goal, this.reward);
	}
}