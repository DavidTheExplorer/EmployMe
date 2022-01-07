package dte.employme.containers;

import static dte.employme.messages.MessageKey.CONTAINER_CLAIM_INSTRUCTION;
import static dte.employme.messages.MessageKey.CONTAINER_HELP_ITEM_NAME;
import static dte.employme.messages.Placeholders.CONTAINER_SUBJECT;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.messages.service.MessageService;
import dte.employme.utils.items.ItemBuilder;

public class PlayerContainerBuilder
{
	String title;
	String[] helpDescription;
	MessageService messageService;
	Collection<ItemStack> initialItems = new ArrayList<>();
	
	public PlayerContainerBuilder of(String subject) 
	{
		Objects.requireNonNull(this.messageService);
		
		this.title = this.messageService.getMessage(CONTAINER_CLAIM_INSTRUCTION)
				.inject(CONTAINER_SUBJECT, subject)
				.first();
		
		return this;
	}

	public PlayerContainerBuilder withHelp(String... helpDescription) 
	{
		this.helpDescription = helpDescription;
		return this;
	}
	
	public PlayerContainerBuilder withMessageService(MessageService messageService) 
	{
		this.messageService = messageService;
		return this;
	}

	public Inventory build() 
	{
		Objects.requireNonNull(this.title);
		Objects.requireNonNull(this.helpDescription);
		Objects.requireNonNull(this.messageService);

		Inventory inventory = Bukkit.createInventory(null, 9 * 6, this.title);

		inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));

		inventory.setItem(53, new ItemBuilder(Material.BOOK)
				.named(this.messageService.getMessage(CONTAINER_HELP_ITEM_NAME).first())
				.withLore(Arrays.stream(this.helpDescription).map(line -> WHITE + line).toArray(String[]::new))
				.createCopy());

		this.initialItems.forEach(inventory::addItem);

		return inventory;
	}
}
