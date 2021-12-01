package dte.employme;

import static dte.employme.job.Job.ORDER_BY_GOAL_NAME;
import static dte.employme.messages.MessageKey.MATERIAL_NOT_FOUND;
import static dte.employme.messages.MessageKey.MUST_BE_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static org.apache.commons.lang.StringUtils.repeat;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.InvalidCommandArgument;
import dte.employme.board.InventoryJobBoard;
import dte.employme.board.JobBoard;
import dte.employme.board.listeners.EmployerNotificationListener;
import dte.employme.board.listeners.JobCompletedMessagesListener;
import dte.employme.board.listeners.JobGoalTransferListener;
import dte.employme.board.listeners.JobRewardGiveListener;
import dte.employme.commands.EmploymentCommand;
import dte.employme.config.ConfigFile;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.containers.service.SimplePlayerContainerService;
import dte.employme.conversations.Conversations;
import dte.employme.inventories.InventoryFactory;
import dte.employme.items.ItemFactory;
import dte.employme.job.SimpleJob;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.service.JobService;
import dte.employme.job.service.SimpleJobService;
import dte.employme.job.subscription.JobSubscriptionService;
import dte.employme.job.subscription.SimpleJobSubscriptionService;
import dte.employme.listeners.JobInventoriesListener;
import dte.employme.listeners.PlayerContainerAbuseListener;
import dte.employme.messages.service.MessageService;
import dte.employme.messages.service.TranslatedMessageService;
import dte.employme.utils.ModernJavaPlugin;
import dte.employme.utils.java.ServiceLocator;
import net.milkbowl.vault.economy.Economy;

public class EmployMe extends ModernJavaPlugin
{
	private Economy economy;
	private JobBoard globalJobBoard;
	private JobService jobService;
	private ItemFactory itemFactory;
	private InventoryFactory inventoryFactory;
	private PlayerContainerService playerContainerService;
	private JobSubscriptionService jobSubscriptionService;
	private MessageService messageService;
	private Conversations conversations;
	
	public static final String CHAT_PREFIX = DARK_GREEN + "[" + GREEN + "EmployMe" + DARK_GREEN + "]";
	
	private static EmployMe INSTANCE;

	@Override
	public void onEnable()
	{
		INSTANCE = this;

		if(!setupEconomy())
		{
			logToConsole(RED + "Economy wasn't found! Shutting Down...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		registerSerializedClasses();
		
		//create the config file
		ConfigFile config = ConfigFile.byPath("config.yml", true);
		
		//create the default(english) language file
		ConfigFile.byPath("languages/english.yml", true);
		
		ConfigFile languageConfig = getLanguageConfig(config.getConfig().getString("Language"));
		
		//if the language defined in the config doesn't have a file, the plugin was disabled by getLanguageConfig() + null was returned
		if(languageConfig == null)
			return;
		
		this.messageService = new TranslatedMessageService(languageConfig);
		this.itemFactory = new ItemFactory();
		
		this.jobSubscriptionService = new SimpleJobSubscriptionService(this.messageService);
		this.jobSubscriptionService.loadSubscriptions();
		
		this.playerContainerService = new SimplePlayerContainerService();
		ServiceLocator.register(PlayerContainerService.class, this.playerContainerService);
		this.playerContainerService.loadContainers();
		
		this.globalJobBoard = new InventoryJobBoard(this.itemFactory, ORDER_BY_GOAL_NAME);
		this.inventoryFactory = new InventoryFactory(this.itemFactory);
		this.jobService = new SimpleJobService(this.globalJobBoard);
		this.conversations = new Conversations(this.globalJobBoard, this.playerContainerService, this.messageService, this.economy);
		
		this.jobService.loadJobs();
		this.globalJobBoard.registerCompleteListener(new JobRewardGiveListener(), new JobGoalTransferListener(this.playerContainerService), new JobCompletedMessagesListener(this.messageService));
		this.globalJobBoard.registerAddListener(new EmployerNotificationListener(this.messageService), this.jobSubscriptionService);

		registerCommands();
		registerListeners(
				new JobInventoriesListener(this.globalJobBoard, this.itemFactory, this.conversations, this.messageService), 
				new PlayerContainerAbuseListener());
		
		new Metrics(this, 13423);
	}

	@Override
	public void onDisable() 
	{
		//TODO: do something with this...
		if(!isEnabled())
			return;
		
		this.jobService.saveJobs();
		this.playerContainerService.saveContainers();
		this.jobSubscriptionService.saveSubscriptions();
	}

	public static EmployMe getInstance()
	{
		return INSTANCE;
	}

	public Economy getEconomy() 
	{
		return this.economy;
	}

	private boolean setupEconomy() 
	{
		if(getServer().getPluginManager().getPlugin("Vault") == null)
			return false;

		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

		if(economyProvider == null)
			return false;

		this.economy = economyProvider.getProvider();
		return true;
	}
	
	private ConfigFile getLanguageConfig(String language) 
	{
		ConfigFile languageConfig = ConfigFile.byPath(String.format("languages/%s.yml", language));
		
		if(!languageConfig.exists()) 
		{
			logToConsole(repeat("-", 55));
			logToConsole(RED + String.format("The messages file for language '%s' is missing!", language));
			logToConsole(RED + "Please close the server to create it. Shutting down until this is fixed!");
			logToConsole(repeat("-", 55));
			
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}
		return languageConfig;
	}

	@SuppressWarnings("deprecation")
	private void registerCommands() 
	{
		BukkitCommandManager commandManager = new BukkitCommandManager(this);
		commandManager.enableUnstableAPI("help");

		//register dependencies
		commandManager.registerDependency(JobBoard.class, this.globalJobBoard);
		commandManager.registerDependency(JobService.class, this.jobService);
		commandManager.registerDependency(InventoryFactory.class, this.inventoryFactory);
		commandManager.registerDependency(PlayerContainerService.class, this.playerContainerService);
		commandManager.registerDependency(JobSubscriptionService.class, this.jobSubscriptionService);
		commandManager.registerDependency(MessageService.class, this.messageService);

		//register conditions
		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			Player player = context.getPlayer();

			if(player.isConversing())
				throw new InvalidCommandArgument(this.messageService.getMessage(MUST_NOT_BE_CONVERSING), false);
		});
		
		commandManager.getCommandConditions().addCondition(Material.class, "Subscribed To Goal", (handler, context, material) -> 
		{
			Player player = context.getPlayer();
			
			if(!this.jobSubscriptionService.isSubscribedTo(player.getUniqueId(), material))
				throw new InvalidCommandArgument(this.messageService.getMessage(MUST_BE_SUBSCRIBED_TO_GOAL), false);
		});
		
		//register contexts
		commandManager.getCommandContexts().registerContext(Material.class, context -> 
		{
			Material material = Material.matchMaterial(context.popFirstArg());
			
			if(material == null) 
				throw new InvalidCommandArgument(this.messageService.getMessage(MATERIAL_NOT_FOUND), false);
			
			return material;
		});

		//register commands
		commandManager.registerCommand(new EmploymentCommand());
	}

	private void registerSerializedClasses() 
	{
		ConfigurationSerialization.registerClass(SimpleJob.class);
		ConfigurationSerialization.registerClass(ItemsReward.class);
		ConfigurationSerialization.registerClass(MoneyReward.class);
	}
}