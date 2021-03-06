package dte.employme.commands;

import static dte.employme.messages.MessageKey.PLUGIN_RELOADED;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.Placeholders.RELOAD_TIME;

import java.time.Duration;
import java.util.List;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.board.displayers.JobBoardDisplayer;
import dte.employme.inventories.JobAddNotifiersGUI;
import dte.employme.inventories.JobContainersGUI;
import dte.employme.inventories.JobCreationGUI;
import dte.employme.inventories.JobDeletionGUI;
import dte.employme.job.Job;
import dte.employme.services.addnotifiers.JobAddedNotifierService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.java.TimingUtils;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("employment|emp")
@Description("The main entry of EmployMe commands.")
public class EmploymentCommand extends BaseCommand
{
	private final Economy economy;
	private final JobBoard globalJobBoard;
	private final JobBoardDisplayer jobBoardDisplayer;
	private final JobRewardService jobRewardService;
	private final JobAddedNotifierService jobAddedNotifierService;
	private final PlayerContainerService playerContainerService;
	private final MessageService messageService;

	public EmploymentCommand(Economy economy, JobBoard globalJobBoard, MessageService messageService, JobRewardService jobRewardService, JobAddedNotifierService jobAddedNotifierService, PlayerContainerService playerContainerService, JobBoardDisplayer jobBoardDisplayer) 
	{
		this.economy = economy;
		this.jobRewardService = jobRewardService;
		this.globalJobBoard = globalJobBoard;
		this.messageService = messageService;
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.playerContainerService = playerContainerService;
		this.jobBoardDisplayer = jobBoardDisplayer;
	}
	
	@Subcommand("view")
	@Description("Search through all the Available Jobs.")
	@CommandPermission("employme.jobs.view")
	public void view(Player player)
	{
		this.jobBoardDisplayer.display(player, this.globalJobBoard);
	}
	
	@Subcommand("offer")
	@Description("Offer a new Job to the public.")
	@CommandPermission("employme.jobs.offer")
	public void offerJob(@Conditions("Not Conversing|Can Offer More Jobs") Player employer)
	{
		new JobCreationGUI(this.globalJobBoard, this.messageService, this.economy, this.playerContainerService, this.jobRewardService).show(employer);
	}

	@Subcommand("delete")
	@Description("Delete a job.")
	@CommandPermission("employme.jobs.delete")
	public void deleteJob(Player player, @Flags("Jobs Able To Delete") List<Job> jobsToDisplay) 
	{
		//TODO: send a MessageKey.NO_JOBS_TO_DISPLAY instead of opening an empty inventory
		new JobDeletionGUI(this.globalJobBoard, jobsToDisplay, this.messageService, this.jobRewardService).show(player);
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
	
	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}
}