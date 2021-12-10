package dte.employme.commands;

import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.MessageKey.THE_JOB_ADDED_NOTIFIERS_ARE;
import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.GOAL_SUBSCRIPTIONS;
import static dte.employme.messages.Placeholders.JOB_ADDED_NOTIFIER;
import static dte.employme.messages.Placeholders.JOB_ADDED_NOTIFIERS;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import dte.employme.board.JobBoard;
import dte.employme.board.displayers.JobBoardDisplayer;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.inventories.InventoryFactory;
import dte.employme.job.Job;
import dte.employme.job.addnotifiers.JobAddedNotifier;
import dte.employme.job.addnotifiers.service.JobAddedNotifierService;
import dte.employme.job.subscription.JobSubscriptionService;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.java.EnumUtils;

@CommandAlias("employment")
@Description("The general employment command - View or Manage them!")
public class EmploymentCommand extends BaseCommand
{
	private final JobBoard globalJobBoard;
	private final InventoryFactory inventoryFactory;
	private final PlayerContainerService playerContainerService;
	private final JobSubscriptionService jobSubscriptionService;
	private final JobAddedNotifierService jobAddedNotifierService;
	private final MessageService messageService;
	private final JobBoardDisplayer jobBoardDisplayer;
	
	public EmploymentCommand(JobBoard globalJobBoard, InventoryFactory inventoryFactory, PlayerContainerService playerContainerService, JobSubscriptionService jobSubscriptionService, JobAddedNotifierService jobAddedNotifierService, MessageService messageService, JobBoardDisplayer jobBoardDisplayer) 
	{
		this.globalJobBoard = globalJobBoard;
		this.inventoryFactory = inventoryFactory;
		this.playerContainerService = playerContainerService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.messageService = messageService;
		this.jobBoardDisplayer = jobBoardDisplayer;
	}

	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}

	@Subcommand("subscribe")
	@Description("Get a notification once a job that rewards a desired item is posted.")
	public void subscribe(Player player, Material material) 
	{
		this.jobSubscriptionService.subscribe(player.getUniqueId(), material);
		this.messageService.sendGeneralMessage(player, SUCCESSFULLY_SUBSCRIBED_TO_GOAL, new Placeholders().put(GOAL, EnumUtils.fixEnumName(material)));
	}

	@Subcommand("unsubscribe")
	@Description("Stop receiving notifications for an item.")
	public void unsubscribe(Player player, @Conditions("Subscribed To Goal") Material material) 
	{
		this.jobSubscriptionService.unsubscribe(player.getUniqueId(), material);
		this.messageService.sendGeneralMessage(player, SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, new Placeholders().put(GOAL, EnumUtils.fixEnumName(material)));
	}

	@Subcommand("mysubscriptions")
	@Description("See your reward subscriptions.")
	public void showSubscriptions(Player player) 
	{
		String subscriptionsNames = this.jobSubscriptionService.getSubscriptions(player.getUniqueId()).stream()
				.map(EnumUtils::fixEnumName)
				.collect(joining(WHITE + ", " + GOLD));

		if(subscriptionsNames.isEmpty())
			subscriptionsNames = this.messageService.getMessage(NONE);

		subscriptionsNames += WHITE + ".";

		this.messageService.sendGeneralMessage(player, YOUR_SUBSCRIPTIONS_ARE, new Placeholders().put(GOAL_SUBSCRIPTIONS, subscriptionsNames));
	}

	@Subcommand("view")
	@Description("Search through all the Available Jobs.")
	public void view(Player player)
	{
		this.jobBoardDisplayer.display(player, this.globalJobBoard);
	}

	@Subcommand("offer")
	@Description("Offer a new Job to the public.")
	@Conditions("Global Jobs Board Not Full")
	public void offerJob(@Conditions("Not Conversing") Player employer) 
	{
		employer.openInventory(this.inventoryFactory.getCreationMenu(employer));
	}

	@Subcommand("delete")
	@Description("Delete a job.")
	public void deleteJob(Player player) 
	{
		List<Job> jobsToDisplay = player.hasPermission("employme.admin.delete") ? this.globalJobBoard.getOfferedJobs() : this.globalJobBoard.getJobsOfferedBy(player.getUniqueId());

		//TODO: send a MessageKey.NO_JOBS_TO_DISPLAY instead of opening an empty inventory
		player.openInventory(this.inventoryFactory.getDeletionMenu(player, this.globalJobBoard, jobsToDisplay));
	}

	@Subcommand("myitems")
	@Description("Claim the items that people gathered for you.")
	public void openContainer(Player employer) 
	{
		employer.openInventory(this.playerContainerService.getItemsContainer(employer.getUniqueId()));
	}

	@Subcommand("myrewards")
	@Description("Claim the rewards you got from Jobs your completed.")
	public void openRewardsContainer(Player player) 
	{
		player.openInventory(this.playerContainerService.getRewardsContainer(player.getUniqueId()));
	}

	@Subcommand("notifier")
	@Syntax("<notifier name>")
	@Description("Choose which notifications you get once a job is created.")
	public void setNotifications(Player player, JobAddedNotifier notifier) 
	{
		this.jobAddedNotifierService.setPlayerNotifier(player.getUniqueId(), notifier);
		this.messageService.sendGeneralMessage(player, YOUR_NEW_JOB_ADDED_NOTIFIER_IS, new Placeholders().put(JOB_ADDED_NOTIFIER, notifier));
	}

	@Subcommand("notifiers list")
	@Description("See the list of notifiers you can select.")
	public void sendNotificationsList(Player player) 
	{
		String notifiersNames = this.jobAddedNotifierService.getNotifiers().stream()
				.map(JobAddedNotifier::getName)
				.collect(joining(WHITE + ", " + GREEN));

		notifiersNames += WHITE + ".";

		this.messageService.sendTo(player, THE_JOB_ADDED_NOTIFIERS_ARE, new Placeholders().put(JOB_ADDED_NOTIFIERS, notifiersNames));
	}
}