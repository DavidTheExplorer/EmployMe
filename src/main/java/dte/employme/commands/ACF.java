package dte.employme.commands;

import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static dte.employme.messages.MessageKey.YOU_OFFERED_TOO_MANY_JOBS;

import java.util.List;

import org.bukkit.entity.Player;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.services.addnotifiers.JobAddedNotifierService;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.PermissionUtils;
import net.milkbowl.vault.economy.Economy;

public class ACF
{
	private final JobBoard globalJobBoard;
	private final Economy economy;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobAddedNotifierService jobAddedNotifierService;
	private final JobSubscriptionService jobSubscriptionService;
	private final PlayerContainerService playerContainerService;
	
	public ACF(JobBoard globalJobBoard, Economy economy, JobService jobService, MessageService messageService, JobAddedNotifierService jobAddedNotifierService, JobSubscriptionService jobSubscriptionService, PlayerContainerService playerContainerService) 
	{
		this.globalJobBoard = globalJobBoard;
		this.economy = economy;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.playerContainerService = playerContainerService;
	}
	
	@SuppressWarnings("deprecation")
	public void setup() 
	{
		BukkitCommandManager commandManager = new BukkitCommandManager(EmployMe.getInstance());
		commandManager.enableUnstableAPI("help");
		
		registerContexts(commandManager);
		registerConditions(commandManager);
		registerCommands(commandManager);
	}
	
	private void registerContexts(BukkitCommandManager commandManager) 
	{
		commandManager.getCommandContexts().registerIssuerOnlyContext(List.class, context -> 
		{
			if(!context.hasFlag("Jobs Able To Delete"))
				return null;
			
			Player player = context.getPlayer();
			
			return player.hasPermission("employme.admin.delete") ? this.globalJobBoard.getOfferedJobs() : this.globalJobBoard.getJobsOfferedBy(player.getUniqueId());
		});
	}
	
	private void registerConditions(BukkitCommandManager commandManager) 
	{
		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			if(context.getPlayer().isConversing())
				throw new InvalidCommandArgument(this.messageService.getMessage(MUST_NOT_BE_CONVERSING).first(), false);
		});
		
		commandManager.getCommandConditions().addCondition(Player.class, "Can Offer More Jobs", (handler, context, player) -> 
		{
			int jobsOffered = this.globalJobBoard.getJobsOfferedBy(player.getUniqueId()).size();
			
			if(jobsOffered >= getAllowedJobsAmount(player))
				throw new ConditionFailedException(this.messageService.getMessage(YOU_OFFERED_TOO_MANY_JOBS).first());
		});
	}
	
	private void registerCommands(BukkitCommandManager commandManager) 
	{
		commandManager.registerCommand(new EmploymentCommand(this.economy, this.globalJobBoard, this.jobService, this.messageService, this.jobAddedNotifierService, this.jobSubscriptionService, this.playerContainerService));
	}
	
	private int getAllowedJobsAmount(Player player) 
	{
		String jobPermission = PermissionUtils.findPermission(player, permission -> permission.startsWith("employme.jobs.allowed.")).orElse("employme.jobs.allowed.3");
		
		return Integer.parseInt(jobPermission.split("\\.")[jobPermission.split("\\.").length-1]);
	}
}
