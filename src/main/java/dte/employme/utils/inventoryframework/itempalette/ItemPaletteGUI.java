package dte.employme.utils.inventoryframework.itempalette;

import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.toMinecraftItem;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;

import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;

public class ItemPaletteGUI extends ChestGui
{
	private PaginatedPane itemsPane;
	
	public ItemPaletteGUI(int rows, String title) 
	{
		super(rows, title);
	}
	
	void init(PaginatedPane itemsPane) 
	{
		this.itemsPane = itemsPane;
	}
	
	public void addItem(ItemStack item)
	{
		/*
		 * InventoryFramework can't handle big items, meaning ItemStack(Material.SNOWBALL, 40) would be shown with the amount of 16.
		 * ItemStackUtils#divideBigItem offers a solution: it splits the item into multiple, each one with the max stack amount.
		 */
		for(ItemStack smallerItem : ItemStackUtils.divide(item))
			InventoryFrameworkUtils.addItem(lastPage -> createStoredItem(smallerItem, lastPage), this.itemsPane, this);
	}

	public List<ItemStack> getStoredItems()
	{
		return this.itemsPane.getItems().stream()
				.map(GuiItem::getItem)
				.collect(toList());
	}
	
	private GuiItem createStoredItem(ItemStack item, OutlinePane page) 
	{
		return new GuiItemBuilder()
				.forItem(item)
				.whenClicked((event, guiItem) -> 
				{
					Map<Integer, ItemStack> left = event.getWhoClicked().getInventory().addItem(toMinecraftItem(guiItem));

					//remove the item if it fitted in the player's inventory
					if(left.isEmpty()) 
						InventoryFrameworkUtils.removeItem(this.itemsPane, page, guiItem);
					else
						guiItem.getItem().setAmount(left.get(0).getAmount());
					
					update();
				})
				.build();
	}
}
