package com.pseudonova.employme;

import static java.util.stream.Collectors.toList;
import static org.bukkit.ChatColor.RED;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.pseudonova.employme.board.JobBoard;
import com.pseudonova.employme.board.inventory.InventoryJobBoard;
import com.pseudonova.employme.board.service.JobBoardService;
import com.pseudonova.employme.board.service.SimpleJobBoardService;
import com.pseudonova.employme.commands.JobsCommand;
import com.pseudonova.employme.listeners.JobInventoryListener;
import com.pseudonova.employme.messages.Message;
import com.pseudonova.employme.reward.ItemsReward;
import com.pseudonova.employme.reward.MoneyReward;
import com.pseudonova.employme.utils.ModernJavaPlugin;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.InvalidCommandArgument;
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
				throw new InvalidCommandArgument("You can't offer an amount of money that you don't have!", false);
		});
		
		commandManager.getCommandConditions().addCondition(Player.class, "Not Conversing", (handler, context, payment) -> 
		{
			Player player = context.getPlayer();
			
			if(player.isConversing())
				throw new InvalidCommandArgument("You have to finish your current conversation.", false);
		});
		
		//register contexts
		commandManager.getCommandContexts().registerContext(MoneyReward.class, context -> 
		{
			try 
			{
				return MoneyReward.of(Double.valueOf(context.popFirstArg()));
			}
			catch(Exception exception)
			{
				throw new InvalidCommandArgument(exception.getMessage(), false);
			}
		});
		
		commandManager.getCommandContexts().registerIssuerOnlyContext(ItemsReward.class, context -> 
		{
			if(!context.hasFlag("Items In Inventory"))
				return null;
			
			List<ItemStack> inventoryItems = Arrays.stream(context.getPlayer().getInventory().getStorageContents())
					.filter(Objects::nonNull)
					.collect(toList());
			
			if(inventoryItems.isEmpty())
				throw new InvalidCommandArgument(Message.ONE_INVENTORY_REWARD_NEEDED.getTemplate(), false);

			return ItemsReward.of(inventoryItems);
		});
		
		//register commands
		commandManager.registerCommand(new JobsCommand(this.jobBoard, this.economy, this.jobBoardService));
	}
}
