package dte.employme;

import static org.bukkit.ChatColor.RED;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.InvalidCommandArgument;
import dte.employme.board.InventoryJobBoard;
import dte.employme.board.JobBoard;
import dte.employme.board.service.JobBoardService;
import dte.employme.board.service.SimpleJobBoardService;
import dte.employme.commands.JobsCommand;
import dte.employme.listeners.JobInventoryListener;
import dte.employme.messages.Message;
import dte.employme.reward.ItemsReward;
import dte.employme.reward.MoneyReward;
import dte.employme.utils.ModernJavaPlugin;
import dte.employme.utils.NumberUtils;
import net.milkbowl.vault.economy.Economy;

public class EmployMe extends ModernJavaPlugin
{
	private JobBoard jobBoard;
	private Economy economy;
	private JobBoardService jobBoardService;

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
		this.jobBoard = new InventoryJobBoard();
		this.jobBoardService = new SimpleJobBoardService();

		registerCommands();
		registerListeners(new JobInventoryListener());
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
		commandManager.registerDependency(JobBoard.class, this.jobBoard);
		commandManager.registerDependency(Economy.class, this.economy);
		commandManager.registerDependency(JobBoardService.class, this.jobBoardService);

		//register conditions
		commandManager.getCommandConditions().addCondition(MoneyReward.class, "payment", (handler, context, payment) -> 
		{
			if(!this.economy.has(context.getPlayer(), payment.getPayment()))
				throw new InvalidCommandArgument(Message.MONEY_REWARD_NOT_ENOUGH.getTemplate(), false);
		});

		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			Player player = context.getPlayer();

			if(player.isConversing())
				throw new InvalidCommandArgument(Message.MUST_NOT_BE_CONVERSING.toString(), false);
		});

		//register contexts
		commandManager.getCommandContexts().registerContext(MoneyReward.class, context -> 
		{
			String paymentText = context.popFirstArg();
			
			return NumberUtils.parseDouble(paymentText)
					.map(MoneyReward::new)
					.orElseThrow(() -> new InvalidCommandArgument(Message.MONEY_REWARD_ERROR_NEGATIVE.getTemplate(), false));
		});

		commandManager.getCommandContexts().registerIssuerOnlyContext(ItemsReward.class, context -> 
		{
			if(!context.hasFlag("Items In Inventory"))
				return null;

			ItemStack[] inventoryItems = Arrays.stream(context.getPlayer().getInventory().getStorageContents())
					.filter(Objects::nonNull)
					.toArray(ItemStack[]::new);

			if(inventoryItems.length == 0)
				throw new InvalidCommandArgument(Message.ONE_INVENTORY_REWARD_NEEDED.getTemplate(), false);

			return new ItemsReward(inventoryItems);
		});

		//register commands
		commandManager.registerCommand(new JobsCommand(this.jobBoard, this.economy, this.jobBoardService));
	}
}
