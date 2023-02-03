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
import dte.employme.configs.MainConfig;
import dte.employme.job.addnotifiers.JobAddNotifier;
import dte.employme.services.job.JobService;
import dte.employme.services.job.addnotifiers.JobAddNotifierService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ACF
{
	private final JobBoard globalJobBoard;
	private final Economy economy;
	private final Permission permission;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobAddNotifierService jobAddNotifierService;
	private final JobSubscriptionService jobSubscriptionService;
	private final PlayerContainerService playerContainerService;
	private final JobAddNotifier defaultNotifier;
	private final MainConfig mainConfig;
	
	public ACF(JobBoard globalJobBoard, Economy economy, Permission permission, JobService jobService, MessageService messageService, JobAddNotifierService jobAddNotifierService, JobSubscriptionService jobSubscriptionService, PlayerContainerService playerContainerService, JobAddNotifier defaultNotifier, MainConfig mainConfig) 
	{
		this.globalJobBoard = globalJobBoard;
		this.economy = economy;
		this.permission = permission;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobAddNotifierService = jobAddNotifierService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.playerContainerService = playerContainerService;
		this.defaultNotifier = defaultNotifier;
		this.mainConfig = mainConfig;
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
			if(player.isOp())
				return;
			
			int jobsOffered = this.globalJobBoard.getJobsOfferedBy(player.getUniqueId()).size();
			int maxJobsAllowed = this.mainConfig.getMaxAllowedJobs(this.permission.getPrimaryGroup(player), 3);
			
			if(jobsOffered >= maxJobsAllowed)
				throw new ConditionFailedException(this.messageService.getMessage(YOU_OFFERED_TOO_MANY_JOBS)
						.inject("max jobs allowed", maxJobsAllowed)
						.first());
		});
	}
	
	private void registerCommands(BukkitCommandManager commandManager) 
	{
		commandManager.registerCommand(new EmploymentCommand(this.economy, this.globalJobBoard, this.jobService, this.messageService, this.jobAddNotifierService, this.jobSubscriptionService, this.playerContainerService, this.defaultNotifier));
	}
}
