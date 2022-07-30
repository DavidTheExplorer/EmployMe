package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.ITEM;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.EnumUtils;

public class UnsubscribeFromItemGUI extends ItemPaletteGUI
{
	public UnsubscribeFromItemGUI(Player player, MessageService messageService, JobSubscriptionService jobSubscriptionService)
	{
		super(messageService.getMessage(INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_TITLE).first(), 
				messageService,
				toUnsubscribeItem(player, messageService, jobSubscriptionService),
				material -> jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material),
				Conversations.createFactory(messageService)
				.withFirstPrompt(new JobGoalPrompt(messageService, messageService.getMessage(INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION).first()))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					Material material = (Material) event.getContext().getSessionData("material");

					unsubscribe(player, material, messageService, jobSubscriptionService);
				}));
	}

	private static Function<Material, GuiItem> toUnsubscribeItem(Player player, MessageService messageService, JobSubscriptionService jobSubscriptionService)
	{
		return material -> 
		{
			String name = messageService.getMessage(INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME)
					.inject(ITEM, EnumUtils.fixEnumName(material))
					.first();
			
			return new GuiItemBuilder()
					.forItem(new ItemBuilder(material)
							.named(name)
							.withLore(messageService.getMessage(INVENTORY_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE).toArray())
							.createCopy())
					.whenClicked(event -> unsubscribe(player, material, messageService, jobSubscriptionService))
					.build();
		};
	}

	private static void unsubscribe(Player player, Material material, MessageService messageService, JobSubscriptionService jobSubscriptionService) 
	{
		jobSubscriptionService.unsubscribe(player.getUniqueId(), material);

		messageService.getMessage(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL)
		.prefixed(messageService.getMessage(PREFIX).first())
		.inject(GOAL, EnumUtils.fixEnumName(material))
		.sendTo(player);

		player.closeInventory();
	}
}