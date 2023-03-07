package dte.employme.job;

import static org.bukkit.ChatColor.RED;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import de.tr7zw.nbtapi.NBTItem;
import dte.employme.EmployMe;
import dte.employme.items.providers.ItemProvider;
import dte.employme.items.providers.MMOItemsProvider;
import dte.employme.items.providers.VanillaProvider;
import dte.employme.rewards.Reward;
import dte.employme.utils.java.MapBuilder;

@SerializableAs("Job")
public class Job implements ConfigurationSerializable
{
	private final UUID uuid;
	private final OfflinePlayer employer;
	private ItemStack goal;
	private ItemProvider goalProvider;
	private Reward reward;

	public Job(OfflinePlayer employer, ItemStack goal, ItemProvider goalProvider, Reward reward) 
	{
		this(UUID.randomUUID(), employer, goal, goalProvider, reward);
	}

	public Job(Map<String, Object> serialized) 
	{
		this(
				UUID.fromString((String) serialized.get("UUID")),
				Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("Employer UUID"))), 
				(ItemStack) serialized.get("Goal"), 
				parseGoalProvider((String) serialized.get("Goal Provider")),
				(Reward) serialized.get("Reward")
				);
	}

	private Job(UUID uuid, OfflinePlayer employer, ItemStack goal, ItemProvider goalProvider, Reward reward) 
	{
		this.uuid = uuid;
		this.employer = employer;
		this.goal = goal;
		this.goalProvider = goalProvider;
		this.reward = reward;
	}
	
	public UUID getUUID() 
	{
		return this.uuid;
	}

	public OfflinePlayer getEmployer() 
	{
		return this.employer;
	}

	public ItemStack getGoal() 
	{
		return new ItemStack(this.goal);
	}
	
	public ItemProvider getGoalProvider()
	{
		return this.goalProvider;
	}

	public Reward getReward() 
	{
		return this.reward;
	}
	
	public void setGoal(ItemStack goal, ItemProvider goalProvider) 
	{
		this.goal = goal;
		this.goalProvider = goalProvider;
	}
	
	public void setReward(Reward reward) 
	{
		this.reward = reward;
	}
	
	public boolean isGoal(ItemStack item) 
	{
		//if the item went through an anvil - ignore any additional tags
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.removeKey("RepairCost");
		ItemStack finalItem = nbtItem.getItem();

		//damaged goals are unacceptable
		if(finalItem.getItemMeta() instanceof Damageable && ((Damageable) finalItem.getItemMeta()).hasDamage())
			return false;
		
		return this.goalProvider.equals(this.goal, finalItem);
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		return new MapBuilder<String, Object>()
				.put("UUID", this.uuid.toString())
				.put("Employer UUID", this.employer.getUniqueId().toString())
				.put("Goal", this.goal)
				.put("Goal Provider", this.goalProvider.getName())
				.put("Reward", this.reward)
				.build();
	}

	@Override
	public String toString()
	{
		return String.format("Job [uuid=%s, employer=%s, goal=%s, goalProvider=%s, reward=%s]", 
				this.uuid,
				this.employer.getUniqueId(),
				this.goal,
				this.goalProvider.getName(), 
				this.reward);
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(this.uuid);
	}

	@Override
	public boolean equals(Object object) 
	{
		if(this == object)
			return true;

		if(!(object instanceof Job))
			return false;

		Job other = (Job) object;

		return Objects.equals(this.uuid, other.uuid);
	}

	private static ItemProvider parseGoalProvider(String goalProvider)
	{
		//backwards compatibility: if goalProvider is not specified, the goal is vanilla
		if(goalProvider == null) 
			return VanillaProvider.INSTANCE;

		ItemProvider parsedProvider;

		switch(goalProvider) 
		{
			case "MMOItems":
				parsedProvider = new MMOItemsProvider();
				break;

			case "Vanilla":
			default:
				parsedProvider = VanillaProvider.INSTANCE;
				break;
		}

		if(!parsedProvider.isAvailable()) 
		{
			EmployMe.getInstance().logToConsole(RED + String.format("One of your jobs uses '%s' as a Goal Provider, but it's not available! Using Vanilla!", goalProvider));
			return VanillaProvider.INSTANCE;
		}

		return parsedProvider;
	}
}