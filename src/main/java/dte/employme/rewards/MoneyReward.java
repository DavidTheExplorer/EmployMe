package dte.employme.rewards;

import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;

import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;
import net.milkbowl.vault.economy.Economy;

@SerializableAs("Money Reward")
public class MoneyReward implements Reward
{
	private final Economy economy;
	private final double payment;

	public MoneyReward(Economy economy, double payment) 
	{
		this.economy = economy;
		this.payment = payment;
	}
	
	public static MoneyReward deserialize(Map<String, Object> serialized) 
	{
		Economy economy = ServiceLocator.getInstance(Economy.class);
		double payment = (double) serialized.get("Payment");
		
		return new MoneyReward(economy, payment);
	}

	@Override
	public void giveTo(OfflinePlayer offlinePlayer)
	{
		this.economy.depositPlayer(offlinePlayer, this.payment);
	}

	public double getPayment() 
	{
		return this.payment;
	}
	
	@Override
	public String getDescription() 
	{
		return String.format("%.2f$", this.payment);
	}

	@Override
	public Map<String, Object> serialize() 
	{
		return new MapBuilder<String, Object>()
				.put("Payment", this.payment)
				.build();
	}

	@Override
	public String toString() 
	{
		return String.format("MoneyReward [payment=%s]", this.payment);
	}
}