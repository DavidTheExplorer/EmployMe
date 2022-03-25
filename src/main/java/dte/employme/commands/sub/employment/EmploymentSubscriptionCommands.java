package dte.employme.commands.sub.employment;

import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.GOAL_SUBSCRIPTIONS;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.java.EnumUtils;

@CommandAlias("employment|emp")
public class EmploymentSubscriptionCommands extends BaseCommand
{
	private final JobSubscriptionService jobSubscriptionService;
	private final MessageService messageService;
	
	public EmploymentSubscriptionCommands(JobSubscriptionService jobSubscriptionService, MessageService messageService) 
	{
		this.jobSubscriptionService = jobSubscriptionService;
		this.messageService = messageService;
	}

	@Subcommand("subscribe")
	@Description("Get a notification once a job that rewards a desired item is posted.")
	@CommandPermission("employme.goals.subscription")
	public void subscribe(Player player, Material material) 
	{
		this.jobSubscriptionService.subscribe(player.getUniqueId(), material);

		this.messageService.getMessage(SUCCESSFULLY_SUBSCRIBED_TO_GOAL)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.inject(GOAL, EnumUtils.fixEnumName(material))
		.sendTo(player);
	}

	@Subcommand("unsubscribe")
	@Description("Stop receiving notifications for an item.")
	@CommandPermission("employme.goals.subscription")
	public void unsubscribe(Player player, @Conditions("Subscribed To Goal") Material material) 
	{
		this.jobSubscriptionService.unsubscribe(player.getUniqueId(), material);

		this.messageService.getMessage(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.inject(GOAL, EnumUtils.fixEnumName(material))
		.sendTo(player);
	}

	@Subcommand("mysubscriptions")
	@Description("See your reward subscriptions.")
	@CommandPermission("employme.goals.subscription")
	public void showSubscriptions(Player player) 
	{
		String subscriptionsNames = this.jobSubscriptionService.getSubscriptions(player.getUniqueId()).stream()
				.map(EnumUtils::fixEnumName)
				.collect(joining(WHITE + ", " + GOLD));

		if(subscriptionsNames.isEmpty())
			subscriptionsNames = this.messageService.getMessage(NONE).first();

		subscriptionsNames += WHITE + ".";

		this.messageService.getMessage(YOUR_SUBSCRIPTIONS_ARE)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.inject(GOAL_SUBSCRIPTIONS, subscriptionsNames)
		.sendTo(player);
	}
}
