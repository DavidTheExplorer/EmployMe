package dte.employme.job;

import java.util.Objects;

import org.bukkit.OfflinePlayer;

import dte.employme.job.goals.Goal;
import dte.employme.job.rewards.Reward;

public class SimpleJob implements Job
{
	private final OfflinePlayer employer;
	private final Goal goal;
	private final Reward reward;

	private SimpleJob(Builder builder) 
	{
		this.employer = builder.employer;
		this.goal = builder.goal;
		this.reward = builder.reward;
	}

	@Override
	public OfflinePlayer getEmployer() 
	{
		return this.employer;
	}

	@Override
	public Goal getGoal() 
	{
		return this.goal;
	}

	@Override
	public Reward getReward() 
	{
		return this.reward;
	}


	public static class Builder
	{
		OfflinePlayer employer;
		Goal goal;
		Reward reward;

		public Builder by(OfflinePlayer employer) 
		{
			this.employer = employer;
			return this;
		}

		public Builder of(Goal goal) 
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
