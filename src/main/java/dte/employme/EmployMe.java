package dte.employme;

import static org.bukkit.ChatColor.RED;

import java.time.Duration;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;

import dte.employme.board.JobBoard;
import dte.employme.board.SimpleJobBoard;
import dte.employme.board.listeners.AutoJobDeleteListeners;
import dte.employme.board.listeners.StopJobLiveUpdatesListener;
import dte.employme.board.listeners.addition.EmployerNotificationListener;
import dte.employme.board.listeners.addition.JobAddDiscordWebhook;
import dte.employme.board.listeners.addition.JobAddNotificationListener;
import dte.employme.board.listeners.completion.JobCompletedMessagesListener;
import dte.employme.board.listeners.completion.JobGoalTransferListener;
import dte.employme.board.listeners.completion.JobRewardGiveListener;
import dte.employme.commands.ACF;
import dte.employme.configs.BlacklistedItemsConfig;
import dte.employme.configs.MainConfig;
import dte.employme.configs.MessagesConfig;
import dte.employme.configs.PlayerContainerConfig;
import dte.employme.job.Job;
import dte.employme.job.addnotifiers.AllJobsNotifier;
import dte.employme.job.addnotifiers.DoNotNotify;
import dte.employme.job.addnotifiers.JobAddNotifier;
import dte.employme.listeners.AutoUpdateListeners;
import dte.employme.messages.MessageProvider;
import dte.employme.papi.EmployMePapiExpansion;
import dte.employme.rewards.MoneyReward;
import dte.employme.services.job.JobService;
import dte.employme.services.job.SimpleJobService;
import dte.employme.services.job.addnotifiers.JobAddNotifierService;
import dte.employme.services.job.addnotifiers.SimpleJobAddNotifierService;
import dte.employme.services.message.ConfigMessageService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.services.playercontainer.SimplePlayerContainerService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.services.rewards.SimpleJobRewardService;
import dte.employme.utils.AutoUpdater;
import dte.employme.utils.java.ServiceLocator;
import dte.employme.utils.java.TimeUtils;
import dte.modernjavaplugin.ModernJavaPlugin;
import dte.spigotconfiguration.SpigotConfig;
import dte.spigotconfiguration.exceptions.ConfigLoadException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class EmployMe extends ModernJavaPlugin
{
	private Economy economy;
	private Permission permission;
	private JobBoard globalJobBoard;
	private JobService jobService;
	private JobRewardService jobRewardService;
	private PlayerContainerService playerContainerService;
	private JobAddNotifierService jobAddNotifierService;
	private MessageService messageService;
	private AutoJobDeleteListeners autoJobDeleteListeners;
	
	private MainConfig mainConfig;
	private BlacklistedItemsConfig blacklistedItemsConfig;
	private SpigotConfig jobsConfig, jobsAutoDeletionConfig, jobAddNotifiersConfig, itemsContainersConfig, messagesConfig;
	
	private static EmployMe INSTANCE;

	@Override
	public void onEnable()
	{
		INSTANCE = this;
		
		//init bukkit services
		try 
		{
			verifyVaultPresent();
			this.economy = loadEconomy();
			this.permission = loadPermission();
		}
		catch(RuntimeException exception) 
		{
			disableWithError(RED + exception.getMessage() + "!", RED + "Shutting down...");
			return;
		}
		ServiceLocator.register(Economy.class, this.economy);
		
		
		
		//init configs
		try 
		{
			SpigotConfig.register(Job.class, MoneyReward.class);

			this.mainConfig = new MainConfig(this);
			this.jobsConfig = SpigotConfig.byPath(this, "boards/global/jobs");
			this.jobsAutoDeletionConfig = SpigotConfig.byPath(this, "boards/global/auto deletion");
			this.jobAddNotifiersConfig = SpigotConfig.byPath(this, "job add notifiers");
			this.blacklistedItemsConfig = new BlacklistedItemsConfig();
			this.itemsContainersConfig = new PlayerContainerConfig(this, "items");
			this.messagesConfig = new MessagesConfig(this, MessageProvider.ENGLISH);
		}
		catch(ConfigLoadException exception) 
		{
			disableWithError(RED + exception.getMessage());
			return;
		}
		
		

		//init the global job board, services, factories, etc.
		this.globalJobBoard = new SimpleJobBoard();
		
		this.messageService = new ConfigMessageService(this.messagesConfig);
		
		this.playerContainerService = new SimplePlayerContainerService(this.itemsContainersConfig, this.messageService);
		this.playerContainerService.loadContainers();
		ServiceLocator.register(PlayerContainerService.class, this.playerContainerService);
		
		this.jobRewardService = new SimpleJobRewardService(this.messageService);
		this.jobService = new SimpleJobService(this.globalJobBoard, this.jobRewardService, this.jobsConfig, this.jobsAutoDeletionConfig, this.blacklistedItemsConfig, this.messageService);
		this.jobService.loadJobs();
		
		this.jobAddNotifierService = new SimpleJobAddNotifierService(this.jobAddNotifiersConfig);
		this.jobAddNotifierService.register(new DoNotNotify());
		this.jobAddNotifierService.register(new AllJobsNotifier(this.messageService));
		this.jobAddNotifierService.loadPlayersNotifiers();
		
		JobAddNotifier defaultJobAddNotifier = this.mainConfig.parseDefaultAddNotifier(this.jobAddNotifierService);
		StopJobLiveUpdatesListener stopJobLiveUpdatesListener = new StopJobLiveUpdatesListener(this.jobService);

		this.globalJobBoard.registerCompleteListener(new JobRewardGiveListener());
		this.globalJobBoard.registerCompleteListener(new JobGoalTransferListener(this.playerContainerService));
		this.globalJobBoard.registerCompleteListener(new JobCompletedMessagesListener(this.messageService, this.jobService, this.mainConfig.getDouble("Partial Job Completions.Notify Employers Above Percentage")));
		this.globalJobBoard.registerCompleteListener(stopJobLiveUpdatesListener);
		this.globalJobBoard.registerAddListener(new EmployerNotificationListener(this.messageService));
		this.globalJobBoard.registerAddListener(new JobAddNotificationListener(this.jobAddNotifierService, defaultJobAddNotifier));
		this.globalJobBoard.registerRemovalListener(stopJobLiveUpdatesListener);
		
		//register commands
		new ACF(this.globalJobBoard, this.economy, this.permission, this.jobService, this.messageService, this.jobAddNotifierService, this.playerContainerService, defaultJobAddNotifier, this.mainConfig).setup();
		
		//setup config features
		setupWebhooks();
		
		//register PlaceholderAPI's placeholders
		registerPapiPlaceholders();
		
		//start metrics
		new Metrics(this, 16573);

		//check for updates
		AutoUpdater.forPlugin(this, 105476)
		.onNewUpdate(newVersion -> registerListeners(new AutoUpdateListeners(this.messageService, newVersion)))
		.onFailedRequest(exception -> logToConsole(RED + "There was an internet error while checking for an update: " + ExceptionUtils.getMessage(exception)))
		.check();
	}
	
	@Override
	public void onDisable()
	{
		this.jobService.saveJobs();
		this.jobService.saveAutoDeletionData();
		this.playerContainerService.saveContainers();
		this.jobAddNotifierService.savePlayersNotifiers();
	}

	public static EmployMe getInstance()
	{
		return INSTANCE;
	}

	private void verifyVaultPresent() 
	{
		if(Bukkit.getPluginManager().getPlugin("Vault") == null)
			throw new RuntimeException("Vault must be installed on the server");
	}

	private Economy loadEconomy() 
	{
		RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);

		if(provider == null)
			throw new RuntimeException("No economy plugin is installed on the server(e.g. EssentialsX)");

		return provider.getProvider();
	}
	
	private Permission loadPermission()
	{
		RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);

		if(provider == null)
			throw new RuntimeException("No permission plugin is installed on the server(e.g. LuckPerms)");
		
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
	
	private void registerPapiPlaceholders() 
	{
		if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			return;
		
		new EmployMePapiExpansion(this.globalJobBoard).register();
	}
	
	@SuppressWarnings("unused")
	private void setupAutoJobDeletion()
	{
		//When running "/emp reload", the listener should be removed & re-added if the config enables it
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