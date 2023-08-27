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
import dte.employme.guis.addnotifiers.JobAddNotifiersGUIFactory;
import dte.employme.guis.board.JobBoardGUIFactory;
import dte.employme.guis.containers.JobContainersGUIFactory;
import dte.employme.guis.creation.JobCreationGUIFactory;
import dte.employme.guis.subscriptions.ItemSubscriptionGUIFactory;
import dte.employme.job.creation.JobCreationContext;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.java.TimeUtils;

@CommandAlias("employment|emp")
@Description("The main entry of EmployMe commands.")
public class EmploymentCommand extends BaseCommand
{
	private final JobBoard globalBoard;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobBoardGUIFactory jobBoardGUIFactory;
	private final JobCreationGUIFactory jobCreationGUIFactory;
	private final JobContainersGUIFactory jobContainersGUIFactory;
	private final JobAddNotifiersGUIFactory jobAddNotifiersGUIFactory;
	private final ItemSubscriptionGUIFactory itemSubscriptionGUIFactory;

	public EmploymentCommand(JobBoard globalBoard, JobService jobService, MessageService messageService, JobBoardGUIFactory jobBoardGUIFactory, JobCreationGUIFactory jobCreationGUIFactory, JobContainersGUIFactory jobContainersGUIFactory, JobAddNotifiersGUIFactory jobAddNotifiersGUIFactory, ItemSubscriptionGUIFactory itemSubscriptionGUIFactory) 
	{
		this.globalBoard = globalBoard;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobBoardGUIFactory = jobBoardGUIFactory;
		this.jobCreationGUIFactory = jobCreationGUIFactory;
		this.jobContainersGUIFactory = jobContainersGUIFactory;
		this.jobAddNotifiersGUIFactory = jobAddNotifiersGUIFactory;
		this.itemSubscriptionGUIFactory = itemSubscriptionGUIFactory;
	}
	
	@Subcommand("%View Name")
	@Description("%View Description")
	@CommandPermission("employme.jobs.view")
	public void view(Player player)
	{
		this.jobBoardGUIFactory.create(player, this.globalBoard).show(player);
	}

	@Subcommand("%Offer Name")
	@Description("%Offer Description")
	@CommandPermission("employme.jobs.offer")
	public void offerJob(@Conditions("Not Conversing|Can Offer More Jobs") Player employer)
	{
		JobCreationContext context = new JobCreationContext();
		context.setEmployer(employer);
		context.setDestinationBoard(this.globalBoard);
		
		this.jobCreationGUIFactory.create(context).show(employer);
	}

	@Subcommand("%MyContainers Name")
	@Description("%MyContainers Description")
	@CommandPermission("employme.mycontainers")
	public void showPersonalContainers(Player player) 
	{
		this.jobContainersGUIFactory.create(player).show(player);
	}

	@Subcommand("%AddNotifiers Name")
	@Description("%AddNotifiers Description")
	@CommandPermission("employme.addnotifiers")
	public void showNotifiers(Player player) 
	{
		this.jobAddNotifiersGUIFactory.create(player).show(player);
	}

	@Subcommand("%MySubscriptions Name")
	@Description("%MySubscriptions Description")
	@CommandPermission("employme.mysubscriptions")
	public void showPersonalSubscriptions(Player player) 
	{
		this.itemSubscriptionGUIFactory.create(player).show(player);
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