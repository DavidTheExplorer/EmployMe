package dte.employme.containers;

import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.utils.items.ItemBuilder;

public class PlayerContainer 
{
	private final Inventory inventory;
	
	private PlayerContainer(Builder builder) 
	{
		this.inventory = builder.inventory;
	}
	
	public Inventory getInventory() 
	{
		return this.inventory;
	}
	
	public void setItem(int slot, ItemStack item) 
	{
		this.inventory.setItem(slot, item);
	}
	
	public void addItem(ItemStack... items) 
	{
		this.inventory.addItem(items);
	}
	
	public ItemStack getItem(int slot) 
	{
		return this.inventory.getItem(slot);
	}
	
	public int size() 
	{
		return this.inventory.getSize();
	}
	
	public static PlayerContainer ofItems() 
	{
		return new Builder()
				.of("Items")
				.withHelp("When someone completes one of your jobs,", "The items they got for you are stored here.")
				.build();
	}
	
	public static PlayerContainer ofRewards() 
	{
		return new Builder()
				.of("Rewards")
				.withHelp("This is where Reward Items are stored", "after you complete a job that pays them.")
				.build();
	}
	
	public static class Builder
	{
		String title;
		String[] helpDescription;
		Collection<ItemStack> initialItems = new ArrayList<>();
		
		Inventory inventory;

		public Builder of(String subject) 
		{
			this.title = String.format("Claim your %s:", subject);
			return this;
		}

		public Builder withHelp(String... helpDescription) 
		{
			this.helpDescription = helpDescription;
			return this;
		}

		public PlayerContainer build() 
		{
			Objects.requireNonNull(this.title);
			Objects.requireNonNull(this.helpDescription);
			
			this.inventory = Bukkit.createInventory(null, 9 * 6, this.title);

			this.inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
			this.inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
			this.inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));

			this.inventory.setItem(53, new ItemBuilder(Material.BOOK)
					.named(GREEN + "Help")
					.withLore(Arrays.stream(this.helpDescription).map(line -> WHITE + line).toArray(String[]::new))
					.createCopy());

			this.initialItems.forEach(this.inventory::addItem);
			
			return new PlayerContainer(this);
		}
	}
}
