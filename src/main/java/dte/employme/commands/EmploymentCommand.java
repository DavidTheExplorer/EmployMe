package dte.employme.commands;

import static dte.employme.messages.MessageKey.PLUGIN_RELOADED;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.Placeholders.RELOAD_TIME;

import java.time.Duration;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.board.displayers.JobBoardDisplayer;
import dte.employme.inventories.JobAddNotifiersGUI;
import dte.employme.inventories.JobContainersGUI;
import dte.employme.services.addnotifiers.JobAddedNotifierService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.java.TimingUtils;

@CommandAlias("employment|emp")
@Description("The main entry of EmployMe commands.")
public class EmploymentCommand extends BaseCommand
{
	private final JobBoard globalJobBoard;
	private final JobBoardDisplayer jobBoardDisplayer;
	private final JobAddedNotifierService jobAddedNotifierService;
	private final PlayerContainerService playerContainerService;
	private final MessageService messageService;

	public EmploymentCommand(JobBoard globalJobBoard, MessageService messageService, JobAddedNotifierService jobAddedNotifierService, PlayerContainerService playerContainerService, JobBoardDisplayer jobBoardDisplayer) 
	{
		this.globalJobBoard = globalJobBoard;
		this.messageService = messageService;
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.playerContainerService = playerContainerService;
		this.jobBoardDisplayer = jobBoardDisplayer;
	}

	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}

	@Subcommand("mycontainers")
	@Description("Claim the items that either people gathered for you OR from completed jobs.")
	@CommandPermission("employme.jobs.mycontainers")
	public void openPersonalContainers(Player player) 
	{
		new JobContainersGUI(this.messageService, playerContainerService).show(player);
	}
	
	@Subcommand("addnotifiers")
	public void showSubscriptions(Player player) 
	{
		new JobAddNotifiersGUI(this.jobAddedNotifierService, this.messageService, player.getUniqueId()).show(player);
	}

	@Subcommand("view")
	@Description("Search through all the Available Jobs.")
	@CommandPermission("employme.jobs.view")
	public void view(Player player)
	{
		this.jobBoardDisplayer.display(player, this.globalJobBoard);
	}

	@Subcommand("reload")
	@CommandPermission("employme.reload")
	public void reload(Player player)
	{
		Duration reloadTime = TimingUtils.time(() -> 
		{
			EmployMe.getInstance().onDisable();
			EmployMe.getInstance().onEnable();
		});

		this.messageService.getMessage(PLUGIN_RELOADED)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.inject(RELOAD_TIME, String.valueOf(reloadTime.toMillis()))
		.sendTo(player);
	}
}