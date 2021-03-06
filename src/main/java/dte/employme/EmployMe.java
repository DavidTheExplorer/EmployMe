package dte.employme;

import static dte.employme.messages.MessageKey.MATERIAL_NOT_FOUND;
import static dte.employme.messages.MessageKey.MUST_BE_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static dte.employme.messages.MessageKey.YOU_OFFERED_TOO_MANY_JOBS;
import static org.bukkit.ChatColor.RED;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import dte.employme.addednotifiers.AllJobsNotifier;
import dte.employme.addednotifiers.DoNotNotify;
import dte.employme.addednotifiers.MaterialSubscriptionNotifier;
import dte.employme.board.SimpleJobBoard;
import dte.employme.board.displayers.InventoryBoardDisplayer;
import dte.employme.board.listenable.EmployerNotificationListener;
import dte.employme.board.listenable.JobAddNotificationListener;
import dte.employme.board.listenable.JobCompletedMessagesListener;
import dte.employme.board.listenable.JobGoalTransferListener;
import dte.employme.board.listenable.JobRewardGiveListener;
import dte.employme.board.listenable.ListenableJobBoard;
import dte.employme.commands.EmploymentCommand;
import dte.employme.commands.sub.employment.EmploymentSubscriptionCommands;
import dte.employme.config.ConfigFile;
import dte.employme.config.ConfigFileFactory;
import dte.employme.config.Messages;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.MoneyReward;
import dte.employme.services.addnotifiers.JobAddedNotifierService;
import dte.employme.services.addnotifiers.SimpleJobAddedNotifierService;
import dte.employme.services.job.JobService;
import dte.employme.services.job.SimpleJobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.job.subscription.SimpleJobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.services.message.TranslatedMessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.services.playercontainer.SimplePlayerContainerService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.services.rewards.SimpleJobRewardService;
import dte.employme.utils.PermissionUtils;
import dte.employme.utils.java.ServiceLocator;
import dte.modernjavaplugin.ModernJavaPlugin;
import net.milkbowl.vault.economy.Economy;

public class EmployMe extends ModernJavaPlugin
{
	private Economy economy;
	private ListenableJobBoard globalJobBoard;
	private JobService jobService;
	private PlayerContainerService playerContainerService;
	private JobSubscriptionService jobSubscriptionService;
	private JobAddedNotifierService jobAddedNotifierService;
	private MessageService messageService;
	private JobRewardService jobRewardService;
	private ConfigFile jobsConfig, subscriptionsConfig, jobAddNotifiersConfig, itemsContainersConfig, rewardsContainersConfig, messagesConfig;

	private static EmployMe INSTANCE;

	@Override
	public void onEnable()
	{
		INSTANCE = this;

		//init economy
		this.economy = getEconomy();
		
		if(this.economy == null) 
		{
			disableWithError(RED + "Economy wasn't found! Shutting Down...");
			return;
		}
		ServiceLocator.register(Economy.class, this.economy);
		
		
		
		//init the configs
		Stream.of(Job.class, MoneyReward.class, ItemsReward.class).forEach(ConfigurationSerialization::registerClass);
		
		ConfigFileFactory configFileFactory = new ConfigFileFactory.Builder()
				.onCreationException((exception, config) -> disableWithError(RED + String.format("Error while creating %s: %s", config.getFile().getName(), exception.getMessage())))
				.onSaveException((exception, config) -> disableWithError(RED + String.format("Error while saving %s: %s", config.getFile().getName(), exception.getMessage())))
				.build();
		
		this.subscriptionsConfig = configFileFactory.loadConfig("subscriptions");
		this.jobAddNotifiersConfig = configFileFactory.loadConfig("job add notifiers");
		this.itemsContainersConfig = configFileFactory.loadContainer("items");
		this.rewardsContainersConfig = configFileFactory.loadContainer("rewards");
		this.messagesConfig = configFileFactory.loadMessagesConfig(Messages.ENGLISH);
		
		if(this.subscriptionsConfig == null || this.jobAddNotifiersConfig == null || this.itemsContainersConfig == null || this.rewardsContainersConfig == null || this.messagesConfig == null)
			return;
		
		
		
		//init the global job board, services, factories, etc.
		this.globalJobBoard = new ListenableJobBoard(new SimpleJobBoard());
		
		this.messageService = new TranslatedMessageService(this.messagesConfig);
		
		this.jobSubscriptionService = new SimpleJobSubscriptionService(this.subscriptionsConfig);
		this.jobSubscriptionService.loadSubscriptions();
		ServiceLocator.register(JobSubscriptionService.class, this.jobSubscriptionService);
		
		this.playerContainerService = new SimplePlayerContainerService(this.itemsContainersConfig, this.rewardsContainersConfig, this.messageService);
		this.playerContainerService.loadContainers();
		ServiceLocator.register(PlayerContainerService.class, this.playerContainerService);
		
		this.jobRewardService = new SimpleJobRewardService(this.messageService);
		
		this.jobsConfig = configFileFactory.loadConfig("jobs");
		
		if(this.jobsConfig == null)
			return;
		
		this.jobService = new SimpleJobService(this.globalJobBoard, this.jobsConfig);
		this.jobService.loadJobs();

		this.jobAddedNotifierService = new SimpleJobAddedNotifierService(this.jobAddNotifiersConfig);
		this.jobAddedNotifierService.register(new DoNotNotify());
		this.jobAddedNotifierService.register(new AllJobsNotifier(this.messageService));
		this.jobAddedNotifierService.register(new MaterialSubscriptionNotifier(this.messageService, this.jobSubscriptionService));
		this.jobAddedNotifierService.loadPlayersNotifiers();

		this.globalJobBoard.registerCompleteListener(new JobRewardGiveListener(), new JobGoalTransferListener(this.playerContainerService), new JobCompletedMessagesListener(this.messageService));
		this.globalJobBoard.registerAddListener(new EmployerNotificationListener(this.messageService), new JobAddNotificationListener(this.jobAddedNotifierService));

		//register commands, listeners, metrics
		registerCommands();

		setDisableListener(() -> 
		{
			this.jobService.saveJobs();
			this.playerContainerService.saveContainers();
			this.jobSubscriptionService.saveSubscriptions();
			this.jobAddedNotifierService.savePlayersNotifiers();
		});
	}

	public static EmployMe getInstance()
	{
		return INSTANCE;
	}

	private Economy getEconomy() 
	{
		if(Bukkit.getPluginManager().getPlugin("Vault") == null)
			return null;

		RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);

		if(provider == null)
			return null;

		return provider.getProvider();
	}

	@SuppressWarnings("deprecation")
	private void registerCommands() 
	{
		BukkitCommandManager commandManager = new BukkitCommandManager(this);
		commandManager.enableUnstableAPI("help");

		//register conditions
		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			if(context.getPlayer().isConversing())
				throw new InvalidCommandArgument(this.messageService.getMessage(MUST_NOT_BE_CONVERSING).first(), false);
		});

		commandManager.getCommandConditions().addCondition(Material.class, "Subscribed To Goal", (handler, context, material) -> 
		{
			if(!this.jobSubscriptionService.isSubscribedTo(context.getPlayer().getUniqueId(), material))
				throw new InvalidCommandArgument(this.messageService.getMessage(MUST_BE_SUBSCRIBED_TO_GOAL).first(), false);
		});
		
		commandManager.getCommandConditions().addCondition(Player.class, "Can Offer More Jobs", (handler, context, player) -> 
		{
			String jobPermission = PermissionUtils.findPermission(player, permission -> permission.startsWith("employme.jobs.allowed."))
					.orElse("employme.jobs.allowed.3");
			
			int allowedJobs = Integer.parseInt(jobPermission.split("\\.")[jobPermission.split("\\.").length-1]);
			
			if(this.globalJobBoard.getJobsOfferedBy(player.getUniqueId()).size() >= allowedJobs)
				throw new ConditionFailedException(this.messageService.getMessage(YOU_OFFERED_TOO_MANY_JOBS).first());
		});
		
		//register contexts
		commandManager.getCommandContexts().registerContext(Material.class, context -> 
		{
			Material material = Material.matchMaterial(context.popFirstArg());

			if(material == null)
				throw new InvalidCommandArgument(this.messageService.getMessage(MATERIAL_NOT_FOUND).first(), false);

			return material;
		});
		
		commandManager.getCommandContexts().registerIssuerOnlyContext(List.class, context -> 
		{
			if(!context.hasFlag("Jobs Able To Delete"))
				return null;
			
			Player player = context.getPlayer();
			
			return player.hasPermission("employme.admin.delete") ? this.globalJobBoard.getOfferedJobs() : this.globalJobBoard.getJobsOfferedBy(player.getUniqueId());
		});

		//register commands
		InventoryBoardDisplayer inventoryBoardDisplayer = new InventoryBoardDisplayer(this.jobService, this.messageService);
		
		commandManager.registerCommand(new EmploymentCommand(this.economy, this.globalJobBoard, this.messageService, this.jobRewardService, this.jobAddedNotifierService, this.playerContainerService, inventoryBoardDisplayer));
		commandManager.registerCommand(new EmploymentSubscriptionCommands(this.jobSubscriptionService, this.messageService));
	}
}