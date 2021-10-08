package dte.employme.job.rewards;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.utils.java.MapBuilder;
import dte.employme.visitors.reward.RewardVisitor;

@SerializableAs("Money Reward")
public class MoneyReward implements Reward
{
	private final double payment;

	public MoneyReward(double payment) 
	{
		this.payment = payment;
	}
	
	public static MoneyReward deserialize(Map<String, Object> serialized) 
	{
		double payment = (double) serialized.get("Payment");
		
		return new MoneyReward(payment);
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

	@Override
	public Map<String, Object> serialize() 
	{
		return new MapBuilder<String, Object>()
				.put("Payment", this.payment)
				.build();
	}
}