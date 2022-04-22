package dte.employme.utils;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;

public class GuiItemBuilder
{
	private ItemStack item;
	private Consumer<InventoryClickEvent> clickListener;
	
	public GuiItemBuilder forItem(ItemStack item) 
	{
		this.item = item;
		return this;
	}
	
	public GuiItemBuilder whenClicked(Consumer<InventoryClickEvent> clickListener) 
	{
		this.clickListener = clickListener;
		return this;
	}
	
	public GuiItem build() 
	{
		return new GuiItem(this.item, this.clickListener); 
	}
}
