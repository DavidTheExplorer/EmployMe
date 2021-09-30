package dte.employme.job.rewards;

import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.visitors.reward.RewardVisitor;

public class MoneyReward implements Reward
{
	private final double payment;

	public MoneyReward(double payment) 
	{
		this.payment = payment;
	}

	@Override
	public void giveTo(Player player) 
	{
		EmployMe.getInstance().getEconomy().depositPlayer(player, this.payment);
	}

	public double getPayment() 
	{
		return this.payment;
	}

	@Override
	public <R> R accept(RewardVisitor<R> visitor) 
	{
		return visitor.visit(this);
	}
}