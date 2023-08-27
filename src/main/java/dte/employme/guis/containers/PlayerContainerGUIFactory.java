package dte.employme.guis.containers;

import org.bukkit.inventory.ItemStack;

import dte.employme.configs.GuiConfig;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteBuilder;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;

public class PlayerContainerGUIFactory 
{
	private final GuiConfig config; 
	
	public PlayerContainerGUIFactory(GuiConfig config) 
	{
		this.config = config;
	}
	
	public ItemPaletteGUI create(String subject) 
	{
		ItemPaletteGUI itemPalette = ItemPaletteBuilder.empty()
				.named(this.config.getTitle().replace("%subject%", subject))
				.withControlButtons(parseBackButton(), parseNextButton())
				.build();
		
		setAbuseListeners(itemPalette);
		
		return itemPalette;
	}
	
	private ItemStack parseBackButton() 
	{
		return this.config.parseGuiItem("back").build().getItem();
	}
	
	private ItemStack parseNextButton() 
	{
		return this.config.parseGuiItem("next").build().getItem();
	}
	
	private void setAbuseListeners(ItemPaletteGUI itemPalette) 
	{
		itemPalette.setOnTopClick(event -> event.setCancelled(true));

		//shifting items into the inventory
		itemPalette.setOnBottomClick(event ->
		{
			if(event.isShiftClick())
				event.setCancelled(true);
		});

		//dragging items into the inventory
		itemPalette.setOnTopDrag(event -> 
		{
			if(event.getOldCursor() == null)
				return;

			event.setCancelled(true);
		});
	}
}
