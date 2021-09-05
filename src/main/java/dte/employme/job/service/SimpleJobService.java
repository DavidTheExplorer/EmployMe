package dte.employme.job.service;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.board.service.JobBoardService;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.conversations.JobPaymentPrompt;
import dte.employme.conversations.JobPostedMessagePrompt;
import dte.employme.messages.Message;
import dte.employme.reward.ItemsReward;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.items.builder.ItemBuilder;
import net.milkbowl.vault.economy.Economy;

public class SimpleJobService implements JobService
{
	private final Map<UUID, Inventory> playersContainers = new HashMap<>();
	
	private final ConversationFactory moneyJobConversationFactory, itemsJobConversationFactory;
	
	private Inventory creationInventory;
	
	public SimpleJobService(JobBoardService jobBoardService, JobBoard jobBoard, Economy economy) 
	{
		this.moneyJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPaymentPrompt(jobBoardService, jobBoard, economy)));
		
		this.itemsJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPostedMessagePrompt(jobBoardService, jobBoard)));
	}
	
	@Override
	public Inventory getContainerOf(Player player) 
	{
		return this.playersContainers.get(player.getUniqueId());
	}
	
	@Override
	public Inventory getCreationInventory() 
	{
		if(this.creationInventory == null)
			this.creationInventory = createCreationInventory();
		
		return this.creationInventory;
	}
	
	private Inventory createCreationInventory() 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Create a new Job");
		
		inventory.setItem(11, new ItemBuilder(Material.GOLD_INGOT, GOLD + "Money Job")
				.newLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay a certain amount of money.")
				.createCopy());
		
		inventory.setItem(15, new ItemBuilder(Material.CHEST, AQUA + "Items Job")
				.newLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy());
		
		InventoryUtils.fillEmptySlots(inventory, InventoryUtils.createWall(Material.BLACK_STAINED_GLASS_PANE));
		
		return inventory;
	}
	
	@Override
	public Optional<Conversation> buildMoneyJobConversation(Player employer)
	{
		return Optional.of(this.moneyJobConversationFactory.buildConversation(employer));
	}

	@Override
	public Optional<Conversation> buildItemsJobConversation(Player employer) 
	{
		ItemStack[] inventoryItems = InventoryUtils.itemsStream(employer.getInventory(), false).toArray(ItemStack[]::new);
		
		if(inventoryItems.length == 0) 
		{
			Message.ONE_INVENTORY_REWARD_NEEDED.sendTo(employer);
			return Optional.empty();
		}
		Conversation conversation = this.itemsJobConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("reward", new ItemsReward(inventoryItems));
		
		return Optional.of(conversation);
	}
	
	private static ConversationFactory createConversationFactory() 
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(false)
				.withModality(false)
				.withPrefix(context -> Message.GENERAL_PREFIX.toString());
	}
}