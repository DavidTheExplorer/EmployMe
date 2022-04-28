package dte.employme.job;

import java.util.Map;
import java.util.Objects;
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

	private Job(Builder builder) 
	{
		this.employer = builder.employer;
		this.goal = builder.goal;
		this.reward = builder.reward;
	}
	
	public Job(Map<String, Object> serialized) 
	{
		this.employer = Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("Employer UUID")));
		this.goal = (ItemStack) serialized.get("Goal");
		this.reward = (Reward) serialized.get("Reward");
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
		return String.format("SimpleJob [employer=%s, goal=%s, reward=%s]", this.employer.getUniqueId().toString(), this.goal, this.reward);
	}
	
	
	
	public static class Builder
	{
		OfflinePlayer employer;
		ItemStack goal;
		Reward reward;

		public Builder byEmployer(OfflinePlayer employer) 
		{
			this.employer = employer;
			return this;
		}

		public Builder withGoal(ItemStack goal) 
		{
			this.goal = goal;
			return this;
		}

		public Builder thatRewards(Reward reward) 
		{
			this.reward = reward;
			return this;
		}

		public Job build() 
		{
			Objects.requireNonNull(this.employer);
			Objects.requireNonNull(this.goal);
			Objects.requireNonNull(this.reward);

			return new Job(this);
		}
	}
}