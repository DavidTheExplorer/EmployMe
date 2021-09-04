package dte.employme.job;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.goal.Goal;
import dte.employme.goal.ItemGoal;
import dte.employme.reward.Reward;
import dte.employme.utils.InventoryUtils;

public class SimpleJob implements Job
{
	private final OfflinePlayer employer;
	private final Goal goal;
	private final Reward reward;
	private final Consumer<Player> onComplete;

	private SimpleJob(Builder builder) 
	{
		this.employer = builder.employer;
		this.goal = builder.goal;
		this.reward = builder.reward;
		this.onComplete = builder.onComplete;
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
	
	@Override
	public boolean hasFinished(Player player)
	{
		return getGoal().hasReached(player);
	}
	
	@Override
	public void onComplete(Player completer) 
	{
		getReward().giveTo(completer);
		
		this.onComplete.accept(completer);
	}


	public static class Builder
	{
		OfflinePlayer employer;
		Goal goal;
		Reward reward;
		Consumer<Player> onComplete;

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

		public Builder ofItem(ItemStack item) 
		{
			return of(new ItemGoal(item))
					.onComplete(completer -> InventoryUtils.remove(completer.getInventory(), item));
		}

		public Builder thatOffers(Reward reward) 
		{
			this.reward = reward;
			return this;
		}

		public Builder onComplete(Consumer<Player> onComplete) 
		{
			this.onComplete = onComplete;
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
