package dte.employme.guis.subscriptions;

import static dte.employme.messages.MessageKey.GOAL_SUBSCRIPTION_QUESTION;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteBuilder;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.EnumUtils;

public class ItemSubscribeGUIFactory
{
	private final GuiConfig config;
	private final JobSubscriptionService jobSubscriptionService;
	private final JobService jobService;
	private final MessageService messageService;

	public ItemSubscribeGUIFactory(GuiConfig config, JobSubscriptionService jobSubscriptionService, JobService jobService, MessageService messageService)
	{
		this.config = config;
		this.jobSubscriptionService = jobSubscriptionService;
		this.jobService = jobService;
		this.messageService = messageService;
	}

	public ChestGui create(Player viewer) 
	{
		return ItemPaletteBuilder.withAllItems()
				.named(this.config.getTitle())
				.filter(material -> !this.jobSubscriptionService.isSubscribedTo(viewer.getUniqueId(), material))
				.map(material -> createSubscribeItem(viewer, material))
				.withControlButtons(parseBackButton(), parseNextButton())
				.withSearchFeature(parseSearchButton(), createSearchConversationFactory(viewer))
				.build();
	}

	private ItemStack parseBackButton() 
	{
		return this.config.parseGuiItem("back").build().getItem();
	}

	private ItemStack parseNextButton() 
	{
		return this.config.parseGuiItem("next").build().getItem();
	}

	private ItemStack parseSearchButton() 
	{
		return this.config.parseGuiItem("search").build().getItem();
	}
	
	private ConversationFactory createSearchConversationFactory(Player viewer) 
	{
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new JobGoalPrompt(this.jobService, this.messageService, this.messageService.loadMessage(GOAL_SUBSCRIPTION_QUESTION).first()))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					Material material = (Material) event.getContext().getSessionData("material");

					subscribe(viewer, material);
				});
	}
	
	private GuiItem createSubscribeItem(Player viewer, Material material)
	{
		ItemStack item = this.config.parseGuiItem("item-template").build().getItem();
		
		item = new ItemBuilder(item)
				.named(item.getItemMeta().getDisplayName().replace("%item%", EnumUtils.fixEnumName(material)))
				.ofType(material)
				.createCopy();

		return new GuiItemBuilder()
				.forItem(item)
				.whenClicked(event -> subscribe(viewer, material))
				.build();
	}
	
	private void subscribe(Player viewer, Material material) 
	{
		this.jobSubscriptionService.subscribe(viewer.getUniqueId(), material);

		this.messageService.loadMessage(SUCCESSFULLY_SUBSCRIBED_TO_GOAL)
		.inject("goal", EnumUtils.fixEnumName(material))
		.sendTo(viewer);

		viewer.closeInventory();
	}
}
