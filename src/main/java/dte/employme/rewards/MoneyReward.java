package dte.employme.rewards;

import static dte.employme.utils.java.Percentages.toFraction;

import java.text.DecimalFormat;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;

import dte.employme.utils.java.MapBuilder;
import dte.employme.utils.java.ServiceLocator;
import net.milkbowl.vault.economy.Economy;

@SerializableAs("Money Reward")
public class MoneyReward implements PartialReward
{
	private final Economy economy;
	private final double payment;

	private static final DecimalFormat PAYMENT_FORMATTER = new DecimalFormat("#.##");

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
	
	public static String formatPayment(MoneyReward moneyReward) 
	{
		return PAYMENT_FORMATTER.format(moneyReward.payment);
	}

	@Override
	public void giveTo(OfflinePlayer offlinePlayer)
	{
		this.economy.depositPlayer(offlinePlayer, this.payment);
	}
	
	@Override
	public MoneyReward afterPartialCompletion(double percentage)
	{
		return new MoneyReward(this.economy, this.payment - (this.payment * toFraction(percentage)));
	}

	public double getPayment() 
	{
		return this.payment;
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