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
import dte.employme.items.custom.CustomItemProvider;
import dte.employme.items.custom.MMOItemsProvider;
import dte.employme.rewards.Reward;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;

public class CustomItemSelectionGUI extends ChestGui
{
	private final GoalCustomizationGUI goalCustomizationGUI;
	private final MessageService messageService;

	public CustomItemSelectionGUI(MessageService messageService, JobSubscriptionService jobSubscriptionService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
	{
		super(1, "Where your item comes from?");

		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOW, 1, 0, 8, 1, new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).named(DARK_RED + "More Plugins Soon!").createCopy())));
		addPane(createCustomPluginsPane());
		update();
	}

	private Pane createCustomPluginsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 8, 1, Priority.NORMAL);
		pane.addItem(createPluginItem(RED + "MMOItems", new MMOItemsProvider()));

		return pane;
	}

	private GuiItem createPluginItem(String pluginName, CustomItemProvider customItemProvider) 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.NAME_TAG)
						.named(pluginName)
						.withLore(WHITE + "A custom item that comes from the " + pluginName + WHITE + " plugin.")
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					Conversations.createFactory(this.messageService)
					.withFirstPrompt(new CustomItemNamePrompt(customItemProvider))
					.addConversationAbandonedListener(abandonedEvent -> 
					{
						if(!abandonedEvent.gracefulExit())
							return;

						ItemStack customItem = (ItemStack) abandonedEvent.getContext().getSessionData("custom item");

						this.goalCustomizationGUI.setItem(customItem);
						this.goalCustomizationGUI.show(player);
					})
					.buildConversation(player)
					.begin();
				})
				.build();
	}
}
