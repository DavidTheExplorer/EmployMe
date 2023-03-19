package dte.employme.commands;

import static dte.employme.messages.MessageKey.PLUGIN_RELOADED;

import java.time.Duration;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.guis.jobs.JobAddNotifiersGUI;
import dte.employme.guis.jobs.JobBoardGUI;
import dte.employme.guis.jobs.creation.JobCreationGUI;
import dte.employme.guis.playercontainer.JobContainersGUI;
import dte.employme.guis.subscriptions.ItemSubscriptionsGUI;
import dte.employme.job.addnotifiers.JobAddNotifier;
import dte.employme.services.job.JobService;
import dte.employme.services.job.addnotifiers.JobAddNotifierService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.java.TimeUtils;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("employment|emp")
@Description("The main entry of EmployMe commands.")
public class EmploymentCommand extends BaseCommand
{
	private final Economy economy;
	private final JobBoard globalJobBoard;
	private final JobService jobService;
	private final JobAddNotifierService jobAddNotifierService;
	private final JobSubscriptionService jobSubscriptionService;
	private final PlayerContainerService playerContainerService;
	private final MessageService messageService;
	private final JobAddNotifier defaultNotifier;

	public EmploymentCommand(Economy economy, JobBoard globalJobBoard, JobService jobService, MessageService messageService, JobAddNotifierService jobAddNotifierService, JobSubscriptionService jobSubscriptionService, PlayerContainerService playerContainerService, JobAddNotifier defaultNotifier) 
	{
		this.economy = economy;
		this.globalJobBoard = globalJobBoard;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobAddNotifierService = jobAddNotifierService;
		this.playerContainerService = playerContainerService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.defaultNotifier = defaultNotifier;
	}
	
	@Subcommand("%View Name")
	@Description("%View Description")
	@CommandPermission("employme.jobs.view")
	public void view(Player player)
	{
		new JobBoardGUI(player, this.globalJobBoard, this.jobService, this.messageService).show(player);
	}
	
	@Subcommand("%Offer Name")
	@Description("%Offer Description")
	@CommandPermission("employme.jobs.offer")
	public void offerJob(@Conditions("Not Conversing|Can Offer More Jobs") Player employer)
	{
		new JobCreationGUI(this.globalJobBoard, this.messageService, this.jobSubscriptionService, this.economy, this.jobService).show(employer);
	}

	@Subcommand("%MyContainers Name")
	@Description("%MyContainers Description")
	@CommandPermission("employme.mycontainers")
	public void showPersonalContainers(Player player) 
	{
		new JobContainersGUI(this.messageService, this.playerContainerService).show(player);
	}
	
	@Subcommand("%AddNotifiers Name")
	@Description("%AddNotifiers Description")
	@CommandPermission("employme.addnotifiers")
	public void showNotifiers(Player player) 
	{
		new JobAddNotifiersGUI(this.jobAddNotifierService, this.messageService, player.getUniqueId(), this.defaultNotifier).show(player);
	}

	@Subcommand("%MySubscriptions Name")
	@Description("%MySubscriptions Description")
	@CommandPermission("employme.mysubscriptions")
	public void showPersonalSubscriptions(Player player) 
	{
		new ItemSubscriptionsGUI(this.jobService, this.messageService, this.jobSubscriptionService).show(player);
	}
	
	@Subcommand("%StopLiveUpdates Name")
	@Description("%StopLiveUpdates Description")
	@CommandPermission("employme.stopliveupdates")
	public void stopLiveUpdates(Player player) 
	{
		this.jobService.stopLiveUpdates(player);
	}

	@Subcommand("%Reload Name")
	@Description("%Reload Description")
	@CommandPermission("employme.reload")
	public void reload(CommandSender sender)
	{
		Duration reloadTime = TimeUtils.time(() -> 
		{
			EmployMe.getInstance().onDisable();
			EmployMe.getInstance().onEnable();
		});

		this.messageService.loadMessage(PLUGIN_RELOADED)
		.inject("reload time", reloadTime.toMillis())
		.sendTo(sender);
	}
	
	@Subcommand("%Help Name")
	@Description("%Help Description")
	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandSender sender, CommandHelp help) 
	{
		help.showHelp();
	}
}