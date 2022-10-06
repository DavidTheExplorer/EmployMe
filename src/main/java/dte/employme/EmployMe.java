package dte.employme;

import static org.bukkit.ChatColor.RED;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;

import dte.employme.addednotifiers.AllJobsNotifier;
import dte.employme.addednotifiers.DoNotNotify;
import dte.employme.addednotifiers.MaterialSubscriptionNotifier;
import dte.employme.board.JobBoard;
import dte.employme.board.SimpleJobBoard;
import dte.employme.board.listeners.AutoJobDeleteListeners;
import dte.employme.board.listeners.addition.EmployerNotificationListener;
import dte.employme.board.listeners.addition.JobAddDiscordWebhook;
import dte.employme.board.listeners.addition.JobAddNotificationListener;
import dte.employme.board.listeners.completion.JobCompletedMessagesListener;
import dte.employme.board.listeners.completion.JobGoalTransferListener;
import dte.employme.board.listeners.completion.JobRewardGiveListener;
import dte.employme.commands.ACF;
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
import dte.employme.utils.java.ServiceLocator;
import dte.employme.utils.java.TimeUtils;
import dte.modernjavaplugin.ModernJavaPlugin;
import net.milkbowl.vault.economy.Economy;

public class EmployMe extends ModernJavaPlugin
{
	private Economy economy;
	private JobBoard globalJobBoard;
	private JobService jobService;
	private JobRewardService jobRewardService;
	private PlayerContainerService playerContainerService;
	private JobSubscriptionService jobSubscriptionService;
	private JobAddedNotifierService jobAddedNotifierService;
	private MessageService messageService;
	private ConfigFile mainConfig, jobsConfig, jobsAutoDeletionConfig, subscriptionsConfig, jobAddNotifiersConfig, itemsContainersConfig, rewardsContainersConfig, messagesConfig;
	private AutoJobDeleteListeners autoJobDeleteListeners;

	private static EmployMe INSTANCE;

	@Override
	public void onEnable()
	{
		INSTANCE = this;

		//init economy
		try 
		{
			this.economy = loadEconomy();
		}
		catch(RuntimeException exception) 
		{
			disableWithError(RED + exception.getMessage() + "!", RED + "Shutting down...");
			return;
		}
		ServiceLocator.register(Economy.class, this.economy);
		
		
		
		//init the configs
		ConfigFileFactory configFileFactory = new ConfigFileFactory.Builder()
				.withSerializables(Job.class, MoneyReward.class, ItemsReward.class)
				.onCreationException((exception, config) -> disableWithError(RED + String.format("Error while creating %s: %s", config.getFile().getName(), exception.getMessage())))
				.onSaveException((exception, config) -> disableWithError(RED + String.format("Error while saving %s: %s", config.getFile().getName(), exception.getMessage())))
				.build();
		
		this.mainConfig = configFileFactory.loadResource("config");
		this.jobsConfig = configFileFactory.loadConfig("boards/global/jobs");
		this.jobsAutoDeletionConfig = configFileFactory.loadConfig("boards/global/auto deletion");
		this.subscriptionsConfig = configFileFactory.loadConfig("subscriptions");
		this.jobAddNotifiersConfig = configFileFactory.loadConfig("job add notifiers");
		this.itemsContainersConfig = configFileFactory.loadContainer("items");
		this.rewardsContainersConfig = configFileFactory.loadContainer("rewards");
		this.messagesConfig = configFileFactory.loadMessagesConfig(Messages.ENGLISH);
		
		if(configFileFactory.anyCreationException()) 
			return;
		
		
		
		//init the global job board, services, factories, etc.
		this.globalJobBoard = new SimpleJobBoard();
		
		this.messageService = new TranslatedMessageService(this.messagesConfig);
		
		this.jobSubscriptionService = new SimpleJobSubscriptionService(this.subscriptionsConfig);
		this.jobSubscriptionService.loadSubscriptions();
		ServiceLocator.register(JobSubscriptionService.class, this.jobSubscriptionService);
		
		this.playerContainerService = new SimplePlayerContainerService(this.itemsContainersConfig, this.rewardsContainersConfig, this.messageService);
		this.playerContainerService.loadContainers();
		ServiceLocator.register(PlayerContainerService.class, this.playerContainerService);
		
		this.jobRewardService = new SimpleJobRewardService(this.messageService);
		this.jobService = new SimpleJobService(this.globalJobBoard, this.jobRewardService, this.jobsConfig, this.jobsAutoDeletionConfig, this.messageService);
		this.jobService.loadJobs();
		
		this.jobAddedNotifierService = new SimpleJobAddedNotifierService(this.jobAddNotifiersConfig);
		this.jobAddedNotifierService.register(new DoNotNotify());
		this.jobAddedNotifierService.register(new AllJobsNotifier(this.messageService));
		this.jobAddedNotifierService.register(new MaterialSubscriptionNotifier(this.messageService, this.jobSubscriptionService));
		this.jobAddedNotifierService.loadPlayersNotifiers();

		this.globalJobBoard.registerCompleteListener(new JobRewardGiveListener(), new JobGoalTransferListener(this.playerContainerService), new JobCompletedMessagesListener(this.messageService, this.jobService));
		this.globalJobBoard.registerAddListener(new EmployerNotificationListener(this.messageService), new JobAddNotificationListener(this.jobAddedNotifierService));

		//register commands, listeners, metrics
		new ACF(this.globalJobBoard, this.economy, this.jobService, this.messageService, this.jobAddedNotifierService, this.jobSubscriptionService, this.playerContainerService).setup();
		setupWebhooks();
		setupAutoJobDeletion();

		setDisableListener(() -> 
		{
			this.jobService.saveJobs();
			this.jobService.saveAutoDeletionData();
			this.playerContainerService.saveContainers();
			this.jobSubscriptionService.saveSubscriptions();
			this.jobAddedNotifierService.savePlayersNotifiers();
		});
	}

	public static EmployMe getInstance()
	{
		return INSTANCE;
	}

	private Economy loadEconomy() 
	{
		if(Bukkit.getPluginManager().getPlugin("Vault") == null)
			throw new RuntimeException("Vault must be installed on the server");
		
		RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);

		if(provider == null)
			throw new RuntimeException("No economy plugin is installed on the server(e.g. EssentialsX)");

		return provider.getProvider();
	}
	
	private void setupWebhooks() 
	{
		ConfigurationSection section = this.mainConfig.getSection("Discord Webhooks.On Job Create");
		
		if(!section.getBoolean("Enabled"))
			return;
		
		String url = section.getString("URL");
		String title = section.getString("Title");
		String message = section.getString("Message");
		
		this.globalJobBoard.registerAddListener(new JobAddDiscordWebhook(url, title, message, this.jobRewardService));		
	}
	
	private void setupAutoJobDeletion()
	{
		//if "/emp reload" was executed after auto deletion was changed to false - remove the listeners
		this.globalJobBoard.removeAddListener(this.autoJobDeleteListeners);
		this.globalJobBoard.removeCompleteListener(this.autoJobDeleteListeners);
		this.globalJobBoard.removeRemovalListener(this.autoJobDeleteListeners);

		ConfigurationSection section = this.mainConfig.getSection("Auto Delete Jobs");
		
		if(!section.getBoolean("Enabled"))
			return;
		
		Duration deleteAfter = TimeUtils.toDuration(section.getString("After"));

		this.jobService.loadAutoDeletionData();
		
		this.autoJobDeleteListeners = new AutoJobDeleteListeners(deleteAfter, this.jobService);
		this.globalJobBoard.registerAddListener(this.autoJobDeleteListeners);
		this.globalJobBoard.registerRemovalListener(this.autoJobDeleteListeners);
		this.globalJobBoard.registerCompleteListener(this.autoJobDeleteListeners);
	}
}