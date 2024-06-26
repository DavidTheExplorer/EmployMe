package dte.employme.guis.jobs.creation;

import static dte.employme.messages.MessageKey.GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_CUSTOM_GOAL_SELECTION_MORE_PLUGINS_SOON_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_CUSTOM_GOAL_SELECTION_TITLE;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.CustomItemNamePrompt;
import dte.employme.items.providers.ItemProvider;
import dte.employme.rewards.Reward;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;

public class CustomItemSelectionGUI extends ChestGui
{
	private final GoalCustomizationGUI goalCustomizationGUI;
	private final MessageService messageService;

	private boolean showGoalCustomizationGUIOnClose = true;

	public CustomItemSelectionGUI(MessageService messageService, JobSubscriptionService jobSubscriptionService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
	{
		super(1, messageService.loadMessage(GUI_CUSTOM_GOAL_SELECTION_TITLE).first());

		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;
		
		setOnClose(event -> 
		{
			if(this.showGoalCustomizationGUIOnClose)
				goalCustomizationGUI.show(event.getPlayer());
		});

		setOnTopClick(event -> event.setCancelled(true));

		addPane(createRectangle(Priority.LOW, 1, 0, 8, 1, new GuiItem(
				new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
				.named(messageService.loadMessage(GUI_CUSTOM_GOAL_SELECTION_MORE_PLUGINS_SOON_ITEM_NAME).first())
				.createCopy())));

		addPane(createCustomPluginsPane());
	}

	private Pane createCustomPluginsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 8, 1, Priority.NORMAL);

		ItemProvider.getAvailable().stream()
		.map(this::createPluginIcon)
		.forEach(pane::addItem);

		return pane;
	}

	private GuiItem createPluginIcon(ItemProvider itemProvider) 
	{
		String name = this.messageService.loadMessage(GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_NAME)
				.inject("item provider", itemProvider.getName())
				.first();
		
		String lore = this.messageService.loadMessage(GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_LORE)
				.inject("item provider", itemProvider.getName())
				.first();

		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.NAME_TAG)
						.named(name)
						.withLore(lore)
						.createCopy())
				.whenClicked(event -> 
				{
					this.showGoalCustomizationGUIOnClose = false;
					
					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					startCustomItemConversation(player, itemProvider);
				})
				.build();
	}
	
	private void startCustomItemConversation(Player player, ItemProvider itemProvider) 
	{
		Conversations.createFactory(this.messageService)
		.withFirstPrompt(new CustomItemNamePrompt(itemProvider))
		.addConversationAbandonedListener(abandonedEvent -> 
		{
			if(!abandonedEvent.gracefulExit())
				return;

			ItemStack customItem = (ItemStack) abandonedEvent.getContext().getSessionData("custom item");
			
			this.goalCustomizationGUI.setCurrentItem(customItem, itemProvider);
			this.goalCustomizationGUI.setRefundRewardOnClose(true);
			this.goalCustomizationGUI.show(player);
		})
		.buildConversation(player)
		.begin();
	}
}
