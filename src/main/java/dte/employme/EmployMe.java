package dte.employme;

import static dte.employme.board.InventoryJobBoard.ORDER_BY_EMPLOYER_NAME;
import static org.bukkit.ChatColor.RED;

import org.bukkit.Bukkit;
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
import dte.employme.commands.JobsCommand;
import dte.employme.conversations.Conversations;
import dte.employme.inventories.InventoryFactory;
import dte.employme.items.ItemFactory;
import dte.employme.job.SimpleJob;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.service.JobService;
import dte.employme.job.service.SimpleJobService;
import dte.employme.listeners.JobInventoriesListener;
import dte.employme.messages.Message;
import dte.employme.utils.ModernJavaPlugin;
import dte.employme.utils.java.ServiceLocator;
import net.milkbowl.vault.economy.Economy;

public class EmployMe extends ModernJavaPlugin
{
	private Economy economy;
	private JobBoard globalJobBoard;
	private JobService jobService;
	private InventoryFactory inventoryFactory;
	private Conversations conversations;

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
		
		this.globalJobBoard = new InventoryJobBoard(ORDER_BY_EMPLOYER_NAME);
		
		this.inventoryFactory = new InventoryFactory(this.globalJobBoard);
		ServiceLocator.register(InventoryFactory.class, this.inventoryFactory);
		
		this.globalJobBoard.registerAddListener(new EmployerNotificationListener());
		this.globalJobBoard.registerCompleteListener(new JobRewardGiveListener(), new JobGoalTransferListener(this.inventoryFactory), new JobCompletedMessagesListener());
		
		this.jobService = new SimpleJobService(this.globalJobBoard);
		this.jobService.loadJobs();
		
		ItemFactory.setup(this.jobService);
		
		this.conversations = new Conversations(this.globalJobBoard, this.inventoryFactory, this.economy);
		
		registerCommands();
		registerListeners(new JobInventoriesListener(this.jobService, this.globalJobBoard, this.conversations));
	}
	
	@Override
	public void onDisable() 
	{
		this.jobService.saveJobs();
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

	@SuppressWarnings("deprecation")
	private void registerCommands() 
	{
		BukkitCommandManager commandManager = new BukkitCommandManager(this);
		commandManager.enableUnstableAPI("help");

		//register dependencies
		commandManager.registerDependency(Economy.class, this.economy);
		commandManager.registerDependency(JobBoard.class, this.globalJobBoard);
		commandManager.registerDependency(JobService.class, this.jobService);
		commandManager.registerDependency(InventoryFactory.class, this.inventoryFactory);

		//register conditions
		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			Player player = context.getPlayer();
			
			if(player.isConversing())
				throw new InvalidCommandArgument(Message.MUST_NOT_BE_CONVERSING.toString(), false);
		});
		
		commandManager.getCommandConditions().addCondition(Player.class, "Employing", (handler, context, payment) -> 
		{
			Player player = context.getPlayer();
			
			if(this.globalJobBoard.getJobsOfferedBy(player.getUniqueId()).isEmpty())
				throw new InvalidCommandArgument(Message.MUST_HAVE_JOBS.toString(), false);
		});
		
		//register commands
		commandManager.registerCommand(new JobsCommand());
	}
	
	private void registerSerializedClasses() 
	{
		ConfigurationSerialization.registerClass(SimpleJob.class);
		ConfigurationSerialization.registerClass(ItemsReward.class);
		ConfigurationSerialization.registerClass(MoneyReward.class);
	}
}
