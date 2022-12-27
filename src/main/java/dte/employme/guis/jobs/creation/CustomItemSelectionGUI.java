package dte.employme.guis.jobs.creation;

import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

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
import dte.employme.items.providers.MMOItemsProvider;
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
		super(1, "Where your item comes from?");

		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;
		
		setOnClose(event -> 
		{
			if(this.showGoalCustomizationGUIOnClose)
				goalCustomizationGUI.show(event.getPlayer());
		});

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOW, 1, 0, 8, 1, new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).named(DARK_RED + "More Plugins Soon!").createCopy())));
		addPane(createCustomPluginsPane());
		update();
	}

	private Pane createCustomPluginsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 8, 1, Priority.NORMAL);
		pane.addItem(createPluginItem(new MMOItemsProvider()));

		return pane;
	}

	private GuiItem createPluginItem(ItemProvider itemProvider) 
	{
		String providerName = itemProvider.getName();
		
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.NAME_TAG)
						.named(RED + providerName)
						.withLore(WHITE + "A custom item that comes from the " + providerName + WHITE + " plugin.")
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
