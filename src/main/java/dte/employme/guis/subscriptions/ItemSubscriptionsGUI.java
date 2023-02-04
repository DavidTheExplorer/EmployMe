package dte.employme.guis.subscriptions;

import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_TITLE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_QUESTION;
import static dte.employme.messages.MessageKey.GUI_SUBSCRIBE_ITEM_PALETTE_TITLE;
import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.guis.ItemPaletteGUI;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.EnumUtils;

public class ItemSubscriptionsGUI extends ChestGui
{
	private final JobService jobService;
	private final MessageService messageService;
	private final JobSubscriptionService jobSubscriptionService;

	public ItemSubscriptionsGUI(JobService jobService, MessageService messageService, JobSubscriptionService jobSubscriptionService) 
	{
		super(3, messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_TITLE).first());
		
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobSubscriptionService = jobSubscriptionService;

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createPanelPane());
	}

	private Pane createPanelPane() 
	{
		OutlinePane pane = new OutlinePane(2, 1, 9, 1);
		pane.setGap(1);

		pane.addItem(createSubscribeItem());
		pane.addItem(createSubscriptionsItem());
		pane.addItem(createUnsubscribeItem());

		return pane;
	}

	private GuiItem createSubscriptionsItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.CHEST)
						.named(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_NAME).first())
						.withLore(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					String subscriptionsNames = this.jobSubscriptionService.getSubscriptions(player.getUniqueId()).stream()
							.map(EnumUtils::fixEnumName)
							.collect(joining(WHITE + ", " + GOLD));

					if(subscriptionsNames.isEmpty())
						subscriptionsNames = this.messageService.getMessage(NONE).first();

					subscriptionsNames += WHITE + ".";

					this.messageService.getMessage(YOUR_SUBSCRIPTIONS_ARE)
					.inject("goal subscriptions", subscriptionsNames)
					.sendTo(player);
				})
				.build();
	}

	private GuiItem createSubscribeItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.WRITABLE_BOOK)
						.named(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_NAME).first())
						.withLore(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();

					new ItemPaletteGUI.Builder(this.messageService.getMessage(GUI_SUBSCRIBE_ITEM_PALETTE_TITLE).first(), this.messageService)
					.transform(toSubscribeItem())
					.filter(material -> !this.jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material))
					.withInitialTypeConversationFactory(Conversations.createFactory(this.messageService)
							.withFirstPrompt(new JobGoalPrompt(this.jobService, this.messageService, this.messageService.getMessage(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_QUESTION).first()))
							.addConversationAbandonedListener(abandonedEvent -> 
							{
								if(!abandonedEvent.gracefulExit())
									return;

								Material material = (Material) abandonedEvent.getContext().getSessionData("material");

								unsubscribe(player, material);
							}))
					.build()
					.show(player);
				})
				.build();
	}

	private GuiItem createUnsubscribeItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.BARRIER)
						.named(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_NAME).first())
						.withLore(this.messageService.getMessage(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();
					
					ItemUnsubscriptionGUI gui = new ItemUnsubscriptionGUI(player, this.jobService, this.messageService, this.jobSubscriptionService);
					gui.setOnClose(closeEvent -> show(player));
					gui.show(event.getWhoClicked());
				})
				.build();
	}

	private Function<Material, GuiItem> toSubscribeItem()
	{
		return material -> 
		{
			String name = this.messageService.getMessage(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_NAME)
					.inject("item", EnumUtils.fixEnumName(material))
					.first();

			return new GuiItemBuilder()
					.forItem(new ItemBuilder(material)
							.named(name)
							.withLore(this.messageService.getMessage(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_LORE).toArray())
							.createCopy())
					.whenClicked(event -> unsubscribe((Player) event.getWhoClicked(), material))
					.build();
		};
	}

	private void unsubscribe(Player player, Material material) 
	{
		this.jobSubscriptionService.subscribe(player.getUniqueId(), material);

		this.messageService.getMessage(SUCCESSFULLY_SUBSCRIBED_TO_GOAL)
		.inject("goal", EnumUtils.fixEnumName(material))
		.sendTo(player);

		player.closeInventory();
	}
}
