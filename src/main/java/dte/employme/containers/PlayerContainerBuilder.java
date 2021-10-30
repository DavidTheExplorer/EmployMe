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

import dte.employme.containers.service.PlayerContainerService;
import dte.employme.utils.items.ItemBuilder;

public class PlayerContainerBuilder
{
	String title;
	String[] helpDescription;
	Collection<ItemStack> initialItems = new ArrayList<>();
	
	public PlayerContainerBuilder of(String subject) 
	{
		this.title = String.format(PlayerContainerService.TITLE_PATTERN, subject);
		return this;
	}

	public PlayerContainerBuilder withHelp(String... helpDescription) 
	{
		this.helpDescription = helpDescription;
		return this;
	}

	public Inventory build() 
	{
		Objects.requireNonNull(this.title);
		Objects.requireNonNull(this.helpDescription);

		Inventory inventory = Bukkit.createInventory(null, 9 * 6, this.title);

		inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));

		inventory.setItem(53, new ItemBuilder(Material.BOOK)
				.named(GREEN + "Help")
				.withLore(Arrays.stream(this.helpDescription).map(line -> WHITE + line).toArray(String[]::new))
				.createCopy());

		this.initialItems.forEach(inventory::addItem);

		return inventory;
	}
}
