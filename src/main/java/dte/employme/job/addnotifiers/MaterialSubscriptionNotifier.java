package dte.employme.job.addnotifiers;

import static dte.employme.messages.MessageKey.JOB_MATERIAL_NOTIFIER_NOTIFICATION;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.java.EnumUtils;

public class MaterialSubscriptionNotifier extends JobAddChatNotifier
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

		return itemsReward.getItems().stream()
				.map(ItemStack::getType)
				.anyMatch(material -> this.jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material));
	}

	@Override
	protected List<MessageBuilder> createMessages(Player player, Job job) 
	{
		String itemsNames = getSubscribedToItemsNames(player, (ItemsReward) job.getReward());

		return Arrays.asList(this.messageService.loadMessage(JOB_MATERIAL_NOTIFIER_NOTIFICATION).inject("rewards", itemsNames));
	}

	private String getSubscribedToItemsNames(Player player, ItemsReward itemsReward) 
	{
		return itemsReward.getItems().stream()
				.map(ItemStack::getType)
				.distinct()
				.filter(material -> this.jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material))
				.map(EnumUtils::fixEnumName)
				.collect(joining(", "));
	}
}
