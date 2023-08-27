package dte.employme.commands;

import static dte.employme.messages.MessageKey.CANNOT_OFFER_MORE_JOBS;
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

import org.bukkit.entity.Player;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.configs.MainConfig;
import dte.employme.guis.addnotifiers.JobAddNotifiersGUIFactory;
import dte.employme.guis.board.JobBoardGUIFactory;
import dte.employme.guis.containers.JobContainersGUIFactory;
import dte.employme.guis.creation.JobCreationGUIFactory;
import dte.employme.guis.subscriptions.ItemSubscriptionGUIFactory;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import net.milkbowl.vault.permission.Permission;

public class ACF
{
	private final MainConfig mainConfig;
	private final Permission permission;
	private final JobBoard globalBoard;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobBoardGUIFactory jobBoardGUIFactory;
	private final JobCreationGUIFactory jobCreationGUIFactory;
	private final JobContainersGUIFactory jobContainersGUIFactory;
	private final JobAddNotifiersGUIFactory jobAddNotifiersGUIFactory;
	private final ItemSubscriptionGUIFactory itemSubscriptionGUIFactory;
	
	public ACF(MainConfig mainConfig, Permission permission, JobBoard globalBoard, JobService jobService, MessageService messageService, JobBoardGUIFactory jobBoardGUIFactory, JobCreationGUIFactory jobCreationGUIFactory, JobContainersGUIFactory jobContainersGUIFactory, JobAddNotifiersGUIFactory jobAddNotifiersGUIFactory, ItemSubscriptionGUIFactory itemSubscriptionGUIFactory) 
	{
		this.mainConfig = mainConfig;
		this.permission = permission;
		this.globalBoard = globalBoard;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobBoardGUIFactory = jobBoardGUIFactory;
		this.jobCreationGUIFactory = jobCreationGUIFactory;
		this.jobContainersGUIFactory = jobContainersGUIFactory;
		this.jobAddNotifiersGUIFactory = jobAddNotifiersGUIFactory;
		this.itemSubscriptionGUIFactory = itemSubscriptionGUIFactory;
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
				throw new InvalidCommandArgument(this.messageService.loadMessage(MUST_NOT_BE_CONVERSING).first(), false);
		});
		
		commandManager.getCommandConditions().addCondition(Player.class, "Can Offer More Jobs", (handler, context, player) -> 
		{
			if(player.isOp())
				return;
			
			int jobsOffered = this.globalBoard.getJobsOfferedBy(player.getUniqueId()).size();
			int maxJobsAllowed = this.mainConfig.getMaxAllowedJobs(this.permission.getPrimaryGroup(player), 3);
			
			if(jobsOffered >= maxJobsAllowed)
				throw new ConditionFailedException(this.messageService.loadMessage(CANNOT_OFFER_MORE_JOBS)
						.inject("max jobs allowed", maxJobsAllowed)
						.first());
		});
	}
	
	private void registerReplacements(BukkitCommandManager commandManager) 
	{
		//Commands Names & Descriptions
		commandManager.getCommandReplacements().addReplacement("View Name", this.messageService.loadMessage(COMMAND_VIEW_NAME).first());
		commandManager.getCommandReplacements().addReplacement("View Description", this.messageService.loadMessage(COMMAND_VIEW_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Offer Name", this.messageService.loadMessage(COMMAND_OFFER_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Offer Description", this.messageService.loadMessage(COMMAND_OFFER_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("MyContainers Name", this.messageService.loadMessage(COMMAND_MYCONTAINERS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("MyContainers Description", this.messageService.loadMessage(COMMAND_MYCONTAINERS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("AddNotifiers Name", this.messageService.loadMessage(COMMAND_ADDNOTIFIERS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("AddNotifiers Description", this.messageService.loadMessage(COMMAND_ADDNOTIFIERS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("MySubscriptions Name", this.messageService.loadMessage(COMMAND_MYSUBSCRIPTIONS_NAME).first());
		commandManager.getCommandReplacements().addReplacement("MySubscriptions Description", this.messageService.loadMessage(COMMAND_MYSUBSCRIPTIONS_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("StopLiveUpdates Name", this.messageService.loadMessage(COMMAND_STOPLIVEUPDATES_NAME).first());
		commandManager.getCommandReplacements().addReplacement("StopLiveUpdates Description", this.messageService.loadMessage(COMMAND_STOPLIVEUPDATES_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Reload Name", this.messageService.loadMessage(COMMAND_RELOAD_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Reload Description", this.messageService.loadMessage(COMMAND_RELOAD_DESCRIPTION).first());
		
		commandManager.getCommandReplacements().addReplacement("Help Name", this.messageService.loadMessage(COMMAND_HELP_NAME).first());
		commandManager.getCommandReplacements().addReplacement("Help Description", this.messageService.loadMessage(COMMAND_HELP_DESCRIPTION).first());
	}
	
	private void registerCommands(BukkitCommandManager commandManager) 
	{
		commandManager.registerCommand(new EmploymentCommand(this.globalBoard, this.jobService, this.messageService, this.jobBoardGUIFactory, this.jobCreationGUIFactory, this.jobContainersGUIFactory, this.jobAddNotifiersGUIFactory, this.itemSubscriptionGUIFactory));
	}
}
