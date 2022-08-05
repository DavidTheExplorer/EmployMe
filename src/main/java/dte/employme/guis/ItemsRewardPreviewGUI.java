package dte.employme.guis;

import static dte.employme.messages.MessageKey.GUI_ITEMS_REWARD_PREVIEW_TITLE;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.rewards.ItemsReward;
import dte.employme.services.message.MessageService;

public class ItemsRewardPreviewGUI extends ChestGui
{
	private final ItemsReward itemsReward;
	
	public ItemsRewardPreviewGUI(Player player, Gui openOnClose, ItemsReward itemsReward, MessageService messageService) 
	{
		super(6, messageService.getMessage(GUI_ITEMS_REWARD_PREVIEW_TITLE).first());
		
		this.itemsReward = itemsReward;
		
		setOnTopClick(event -> event.setCancelled(true));
		setOnClose(event -> openOnClose.show(player));
		addPane(createItemsPane());
		update();
	}
	
	private Pane createItemsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, Priority.LOWEST);
		
		this.itemsReward.getItems().stream()
		.map(item -> new GuiItem(new ItemStack(item)))
		.forEach(pane::addItem);
		
		return pane;
	}
}
