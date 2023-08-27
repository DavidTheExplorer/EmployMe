package dte.employme.guis.board;

import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.rewards.ItemsReward;

public class ItemsRewardPreviewGUIFactory
{
	private final GuiConfig config;
	
	public ItemsRewardPreviewGUIFactory(GuiConfig config) 
	{
		this.config = config;
	}
	
	public ChestGui create(ItemsReward itemsReward) 
	{
		ChestGui gui = new ChestGui(6, this.config.getTitle());
		
		//add panes
		gui.addPane(createDisplayPane(itemsReward));
		
		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		
		return gui;
	}
	
	private Pane createDisplayPane(ItemsReward itemsReward) 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, Priority.LOWEST);
		
		itemsReward.getItems().stream()
		.map(item -> new GuiItem(new ItemStack(item)))
		.forEach(pane::addItem);
		
		return pane;
	}
}
