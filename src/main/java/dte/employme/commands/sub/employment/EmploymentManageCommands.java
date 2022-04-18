package dte.employme.commands.sub.employment;

import java.util.List;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.board.JobBoard;
import dte.employme.inventories.JobCreationGUI;
import dte.employme.inventories.JobDeletionGUI;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.services.rewards.JobRewardService;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("employment|emp")
public class EmploymentManageCommands extends BaseCommand
{
	private final JobBoard globalJobBoard;
	private final Economy economy;
	private final JobRewardService jobRewardService;
	private final MessageService messageService;
	private final PlayerContainerService playerContainerService;
	
	public EmploymentManageCommands(JobBoard globalJobBoard, Economy economy, JobRewardService jobRewardService, MessageService messageService, PlayerContainerService playerContainerService) 
	{
		this.globalJobBoard = globalJobBoard;
		this.economy = economy;
		this.jobRewardService = jobRewardService;
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
	}

	@Subcommand("offer")
	@Description("Offer a new Job to the public.")
	@Conditions("Global Jobs Board Not Full")
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
}
