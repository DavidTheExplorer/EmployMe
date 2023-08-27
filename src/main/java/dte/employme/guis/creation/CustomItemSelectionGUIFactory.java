package dte.employme.guis.creation;

import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.CustomItemNamePrompt;
import dte.employme.guis.creation.GoalCustomizationGUIFactory.GoalCustomizationGUI;
import dte.employme.items.providers.ItemProvider;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.RespectingChestGui;
import dte.employme.utils.items.ItemBuilder;

public class CustomItemSelectionGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;

	public CustomItemSelectionGUIFactory(GuiConfig config, MessageService messageService) 
	{
		this.config = config;
		this.messageService = messageService;
	}

	public ChestGui create(Player viewer, GoalCustomizationGUI goalCustomizationGUI) 
	{
		RespectingChestGui gui = new RespectingChestGui(new ChestGui(1, this.config.getTitle()), goalCustomizationGUI);

		//add panes
		gui.addPane(parseMoreItemsSoonPane());
		gui.addPane(parseCustomPluginsPane(viewer, gui, goalCustomizationGUI));

		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));

		return gui;
	}
	
	
	
	/*
	 * Panes
	 */
	private Pane parseMoreItemsSoonPane() 
	{
		GuiItem item = this.config.parseGuiItem("more-plugins-soon").build();

		return createRectangle(Priority.LOW, 1, 0, 8, 1, item);
	}
	
	private Pane parseCustomPluginsPane(Player viewer, RespectingChestGui customItemSelectionGUI, GoalCustomizationGUI goalCustomizationGUI) 
	{
		OutlinePane pane = new OutlinePane(0, 0, 8, 1, Priority.NORMAL);

		ItemProvider.getAvailable().stream()
		.map(provider -> parsePluginIcon(viewer, customItemSelectionGUI, provider, goalCustomizationGUI))
		.forEach(pane::addItem);

		return pane;
	}
	
	
	
	/*
	 * Items
	 */
	private GuiItem parsePluginIcon(Player viewer, RespectingChestGui customItemSelectionGUI, ItemProvider itemProvider, GoalCustomizationGUI goalCustomizationGUI) 
	{
		ItemStack item = this.config.parseGuiItem("item-provider").build().getItem();
		
		//replace the %item provider% placeholder in the name
		String newName = item.getItemMeta().getDisplayName().replace("%item provider%", itemProvider.getName());
		
		//replace the %item provider% placeholder in the lore
		String[] newLore = item.getItemMeta().getLore().stream()
				.map(line -> line.replace("%item provider%", itemProvider.getName()))
				.toArray(String[]::new);
		
		item = new ItemBuilder(item)
				.named(newName)
				.withLore(newLore)
				.createCopy();

		return new GuiItemBuilder()
				.forItem(item)
				.whenClicked(event -> 
				{
					customItemSelectionGUI.removeParent();
					viewer.closeInventory();

					createCustomItemConversation(viewer, itemProvider, goalCustomizationGUI).begin();
				})
				.build();
	}

	private Conversation createCustomItemConversation(Player viewer, ItemProvider itemProvider, GoalCustomizationGUI goalCustomizationGUI) 
	{
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new CustomItemNamePrompt(itemProvider))
				.addConversationAbandonedListener(abandonedEvent -> 
				{
					if(!abandonedEvent.gracefulExit())
						return;

					ItemStack customItem = (ItemStack) abandonedEvent.getContext().getSessionData("custom item");

					goalCustomizationGUI.setCurrentItem(customItem, itemProvider);
					goalCustomizationGUI.refundRewardOnClose(true);
					goalCustomizationGUI.show(viewer);
				})
				.buildConversation(viewer);
	}
}
