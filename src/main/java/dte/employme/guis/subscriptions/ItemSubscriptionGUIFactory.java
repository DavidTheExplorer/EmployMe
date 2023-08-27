package dte.employme.guis.subscriptions;

import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;

import dte.employme.configs.GuiConfig;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.java.EnumUtils;

public class ItemSubscriptionGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;
	private final JobSubscriptionService jobSubscriptionService;
	private final ItemSubscribeGUIFactory itemSubscribeGUIFactory;
	private final ItemUnsubscribeGUIFactory itemUnsubscribeGUIFactory;

	public ItemSubscriptionGUIFactory(GuiConfig config, ItemSubscribeGUIFactory itemSubscribeGUIFactory, ItemUnsubscribeGUIFactory itemUnsubscribeGUIFactory, JobSubscriptionService jobSubscriptionService, MessageService messageService) 
	{
		this.config = config;
		this.itemSubscribeGUIFactory = itemSubscribeGUIFactory;
		this.itemUnsubscribeGUIFactory = itemUnsubscribeGUIFactory;
		this.jobSubscriptionService = jobSubscriptionService;
		this.messageService = messageService;
	}

	public ChestGui create(Player viewer)
	{
		ChestGui gui = new ChestGui(3, this.config.getTitle());
		
		//add panes
		gui.addPane(parseBackground());
		gui.addPane(createPanelPane(viewer));
		
		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));

		return gui;
	}

	private OutlinePane parseBackground()
	{
		OutlinePane pane = new OutlinePane(Slot.fromIndex(0), 9, 3, Priority.LOWEST);
		pane.setRepeat(true);
		pane.addItem(this.config.parseGuiItem("background").build());

		return pane;
	}

	private Pane createPanelPane(Player viewer) 
	{
		OutlinePane pane = new OutlinePane(2, 1, 9, 1);
		pane.setGap(1);

		pane.addItem(parseSubscribeItem(viewer));
		pane.addItem(parseYourSubscriptionsItem(viewer));
		pane.addItem(parseUnsubscribeItem(viewer));

		return pane;
	}

	private GuiItem parseYourSubscriptionsItem(Player viewer) 
	{
		return this.config.parseGuiItem("your-subscriptions")
				.whenClicked(event -> 
				{
					viewer.closeInventory();

					String subscriptionsNames = this.jobSubscriptionService.getSubscriptions(viewer.getUniqueId()).stream()
							.map(EnumUtils::fixEnumName)
							.collect(joining(WHITE + ", " + GOLD));

					if(subscriptionsNames.isEmpty())
						subscriptionsNames = this.messageService.loadMessage(NONE).first();

					subscriptionsNames += WHITE + ".";

					this.messageService.loadMessage(YOUR_SUBSCRIPTIONS_ARE)
					.inject("goal subscriptions", subscriptionsNames)
					.sendTo(viewer);
				})
				.build();
	}

	private GuiItem parseSubscribeItem(Player viewer) 
	{
		return this.config.parseGuiItem("subscribe")
				.whenClicked(event -> this.itemSubscribeGUIFactory.create(viewer).show(viewer))
				.build();
	}

	private GuiItem parseUnsubscribeItem(Player viewer) 
	{
		return this.config.parseGuiItem("unsubscribe")
				.whenClicked(event -> this.itemUnsubscribeGUIFactory.create(viewer).show(viewer))
				.build();
	}
}
