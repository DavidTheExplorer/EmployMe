package dte.employme.guis.subscriptions;

import static dte.employme.messages.MessageKey.GUI_UNSUBSCRIBE_ITEM_PALETTE_TITLE;
import static dte.employme.messages.MessageKey.GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.guis.ItemPaletteGUI;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.EnumUtils;

public class ItemUnsubscriptionGUI extends ItemPaletteGUI
{
	public ItemUnsubscriptionGUI(Player player, JobService jobService, MessageService messageService, JobSubscriptionService jobSubscriptionService)
	{
		super(messageService.loadMessage(GUI_UNSUBSCRIBE_ITEM_PALETTE_TITLE).first(), 
				messageService,
				toUnsubscribeItem(player, messageService, jobSubscriptionService),
				material -> jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material),
				Conversations.createFactory(messageService)
				.withFirstPrompt(new JobGoalPrompt(jobService, messageService, messageService.loadMessage(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION).first()))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					Material material = (Material) event.getContext().getSessionData("material");

					unsubscribe(player, material, messageService, jobSubscriptionService);
				}));
		
		setOnTopClick(event -> event.setCancelled(true));
	}

	private static Function<Material, GuiItem> toUnsubscribeItem(Player player, MessageService messageService, JobSubscriptionService jobSubscriptionService)
	{
		return material -> 
		{
			String name = messageService.loadMessage(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME)
					.inject("item", EnumUtils.fixEnumName(material))
					.first();
			
			return new GuiItemBuilder()
					.forItem(new ItemBuilder(material)
							.named(name)
							.withLore(messageService.loadMessage(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE).toArray())
							.createCopy())
					.whenClicked(event -> unsubscribe(player, material, messageService, jobSubscriptionService))
					.build();
		};
	}

	private static void unsubscribe(Player player, Material material, MessageService messageService, JobSubscriptionService jobSubscriptionService) 
	{
		jobSubscriptionService.unsubscribe(player.getUniqueId(), material);

		messageService.loadMessage(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL)
		.inject("goal", EnumUtils.fixEnumName(material))
		.sendTo(player);

		player.closeInventory();
	}
}