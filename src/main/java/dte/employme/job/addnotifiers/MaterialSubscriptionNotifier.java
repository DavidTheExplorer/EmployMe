package dte.employme.job.addnotifiers;

import static dte.employme.messages.MessageKey.SUBSCRIBED_TO_GOALS_NOTIFICATION;
import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.subscription.JobSubscriptionService;
import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.java.EnumUtils;
import dte.employme.utils.java.MapBuilder;

public class MaterialSubscriptionNotifier extends JobAddedChatNotifier
{
	private final JobSubscriptionService jobSubscriptionService;

	public MaterialSubscriptionNotifier(MessageService messageService, JobSubscriptionService jobSubscriptionService)
	{
		super("Material Subscriptions", messageService);

		this.jobSubscriptionService = jobSubscriptionService;
	}

	@Override
	public boolean shouldNotify(Player player, Job job) 
	{
		if(!(job.getReward() instanceof ItemsReward))
			return false;
		
		ItemsReward itemsReward = (ItemsReward) job.getReward();
		UUID playerUUID = player.getUniqueId();

		return itemsReward.getItems()
				.stream()
				.map(ItemStack::getType)
				.anyMatch(material -> this.jobSubscriptionService.isSubscribedTo(playerUUID, material));
	}

	@Override
	protected Map<MessageKey, Map<String, String>> createMessages(Player player, Job job) 
	{
		ItemsReward itemsReward = (ItemsReward) job.getReward();
		
		return new MapBuilder<MessageKey, Map<String, String>>()
				.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, new MapBuilder<String, String>().put(Placeholders.REWARDS, getSubscribedToItemsNames(player, itemsReward)).build())
				.build();
	}
	
	private String getSubscribedToItemsNames(Player player, ItemsReward itemsReward) 
	{
		UUID playerUUID = player.getUniqueId();
		
		return itemsReward.getItems().stream()
				.map(ItemStack::getType)
				.distinct() //TODO: move to be after the filter
				.filter(material -> this.jobSubscriptionService.isSubscribedTo(playerUUID, material))
				.map(EnumUtils::fixEnumName)
				.collect(joining(", "));
	}
}
