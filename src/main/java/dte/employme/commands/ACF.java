package dte.employme.commands;

import static dte.employme.messages.MessageKey.COMMAND_ADDNOTIFIERS_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_ADDNOTIFIERS_NAME;
import static dte.employme.messages.MessageKey.COMMAND_HELP_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_HELP_NAME;
import static dte.employme.messages.MessageKey.COMMAND_MYCONTAINERS_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_MYCONTAINERS_NAME;
import static dte.employme.messages.MessageKey.COMMAND_MYSUBSCRIPTIONS_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_MYSUBSCRIPTIONS_NAME;
import static dte.employme.messages.MessageKey.COMMAND_OFFER_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_OFFER_NAME;
import static dte.employme.messages.MessageKey.COMMAND_RELOAD_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_RELOAD_NAME;
import static dte.employme.messages.MessageKey.COMMAND_STOPLIVEUPDATES_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_STOPLIVEUPDATES_NAME;
import static dte.employme.messages.MessageKey.COMMAND_VIEW_DESCRIPTION;
import static dte.employme.messages.MessageKey.COMMAND_VIEW_NAME;
import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static dte.employme.messages.MessageKey.YOU_OFFERED_TOO_MANY_JOBS;

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
		
		registerConditions(commandManager);
		registerReplacements(commandManager);
		registerCommands(commandManager);
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
	
	private void registerReplacements(BukkitCommandManager commandManager) 
	{
		//Commands Names & Descriptions
		commandManager.getCommandReplacements().addReplacement("View Name", this.messageService.getMessage(COMMAND_VIEW_NAME).first());
		commandManager.getCommandReplacements().addReplacement("View Description", this.messageService.getMessage(COMMAND_VIEW_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Offer Name", this.messageService.getMessage(COMMAND_OFFER_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Offer Description", this.messageService.getMessage(COMMAND_OFFER_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("MyContainers Name", this.messageService.getMessage(COMMAND_MYCONTAINERS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("MyContainers Description", this.messageService.getMessage(COMMAND_MYCONTAINERS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("AddNotifiers Name", this.messageService.getMessage(COMMAND_ADDNOTIFIERS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("AddNotifiers Description", this.messageService.getMessage(COMMAND_ADDNOTIFIERS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("MySubscriptions Name", this.messageService.getMessage(COMMAND_MYSUBSCRIPTIONS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("MySubscriptions Description", this.messageService.getMessage(COMMAND_MYSUBSCRIPTIONS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("StopLiveUpdates Name", this.messageService.getMessage(COMMAND_STOPLIVEUPDATES_NAME).first());
		commandManager.getCommandReplacements().addReplacement("StopLiveUpdates Description", this.messageService.getMessage(COMMAND_STOPLIVEUPDATES_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Reload Name", this.messageService.getMessage(COMMAND_RELOAD_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Reload Description", this.messageService.getMessage(COMMAND_RELOAD_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Help Name", this.messageService.getMessage(COMMAND_HELP_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Help Description", this.messageService.getMessage(COMMAND_HELP_DESCRIPTION).first());
	}
	
	private void registerCommands(BukkitCommandManager commandManager) 
	{
		commandManager.registerCommand(new EmploymentCommand(this.economy, this.globalJobBoard, this.jobService, this.messageService, this.jobAddNotifierService, this.jobSubscriptionService, this.playerContainerService, this.defaultNotifier));
	}
}
