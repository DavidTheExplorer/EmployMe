package dte.employme.job;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.rewards.Reward;
import dte.employme.utils.java.MapBuilder;

@SerializableAs("Job")
public class SimpleJob implements Job
{
	private final OfflinePlayer employer;
	private final ItemStack goal;
	private final Reward reward;

	private SimpleJob(Builder builder) 
	{
		this.employer = builder.employer;
		this.goal = builder.goal;
		this.reward = builder.reward;
	}
	
	public SimpleJob(Map<String, Object> serialized) 
	{
		this.employer = Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("Employer UUID")));
		this.goal = (ItemStack) serialized.get("Goal");
		this.reward = (Reward) serialized.get("Reward");
	}

	@Override
	public OfflinePlayer getEmployer() 
	{
		return this.employer;
	}

	@Override
	public ItemStack getGoal() 
	{
		return new ItemStack(this.goal);
	}

	@Override
	public Reward getReward() 
	{
		return this.reward;
	}
	
	@Override
	public boolean hasFinished(Player player) 
	{
		return player.getInventory().containsAtLeast(this.goal, this.goal.getAmount());
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


	public static class Builder
	{
		OfflinePlayer employer;
		ItemStack goal;
		Reward reward;

		public Builder by(OfflinePlayer employer) 
		{
			this.employer = employer;
			return this;
		}

		public Builder of(ItemStack goal) 
		{
			this.goal = goal;
			return this;
		}

		public Builder thatOffers(Reward reward) 
		{
			this.reward = reward;
			return this;
		}

		public SimpleJob build() 
		{
			Objects.requireNonNull(this.employer);
			Objects.requireNonNull(this.goal);
			Objects.requireNonNull(this.reward);

			return new SimpleJob(this);
		}
	}
}
