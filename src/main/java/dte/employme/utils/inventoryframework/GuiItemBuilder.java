package dte.employme.utils.inventoryframework;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;

public class GuiItemBuilder
{
	private ItemStack item;

	//Sometimes the listener needs access to the GuiItem itself, that's why GuiItem is also passed
	private BiConsumer<InventoryClickEvent, GuiItem> clickListener;

	public GuiItemBuilder forItem(ItemStack item) 
	{
		this.item = item;
		return this;
	}

	public GuiItemBuilder whenClicked(Consumer<InventoryClickEvent> clickListener) 
	{
		this.clickListener = (event, guiItem) -> clickListener.accept(event);
		return this;
	}

	public GuiItemBuilder whenClicked(BiConsumer<InventoryClickEvent, GuiItem> clickListener) 
	{
		this.clickListener = clickListener;
		return this;
	}

	public GuiItem build()
	{
		Objects.requireNonNull(this.item, "The item cannot be null");

		GuiItem guiItem = new GuiItem(this.item);
		guiItem.setAction(event -> this.clickListener.accept(event, guiItem));

		return guiItem;
	}
}
