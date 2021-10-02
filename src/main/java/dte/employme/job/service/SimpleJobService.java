package dte.employme.job.service;

import static dte.employme.utils.InventoryUtils.createWall;
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
import dte.employme.items.ItemFactory;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Message;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.visitors.goal.TextGoalDescriptor;
import dte.employme.visitors.reward.TextRewardDescriptor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private final ConversationFactory moneyJobConversationFactory, itemsJobConversationFactory;
	
	private final Map<UUID, Inventory> rewardsContainers = new HashMap<>();
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>();
	
	private Inventory creationInventory;
	
	public SimpleJobService(JobBoard globalJobBoard, JobBoardService jobBoardService, Economy economy) 
	{
		this.globalJobBoard = globalJobBoard;
		
		this.moneyJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPaymentPrompt(jobBoardService, globalJobBoard, economy)));
		
		this.itemsJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPostedMessagePrompt(jobBoardService, globalJobBoard)));
	}
	
	@Override
	public void onComplete(Job job, Player completer)
	{
		this.globalJobBoard.removeJob(job);
		
		job.getGoal().onReach(completer);
		job.getReward().giveTo(completer);
		
		//message the completer
		Message.sendGeneralMessage(completer, Message.JOB_SUCCESSFULLY_COMPLETED);
		
		//notify the employer
		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> employer.spigot().sendMessage(new ComponentBuilder(Message.GENERAL_PREFIX + Message.PLAYER_COMPLETED_YOUR_JOB.inject(completer.getName()))
				.event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job))))
				.create()));
	}
	
	@Override
	public boolean hasFinished(Job job, Player player)
	{
		return job.getGoal().hasReached(player);
	}
	
	@Override
	public Inventory getCreationInventory(Player employer)
	{
		if(this.creationInventory == null)
			this.creationInventory = createCreationInventory();
		
		return this.creationInventory;
	}
	
	@Override
	public Inventory getDeletionInventory(Player employer)
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Select Jobs to Delete");

		this.globalJobBoard.getJobsOfferedBy(employer.getUniqueId()).stream()
		.map(job -> ItemFactory.createDeletionIcon(this.globalJobBoard, job))
		.forEach(inventory::addItem);

		InventoryUtils.fillEmptySlots(inventory, InventoryUtils.createWall(Material.BLACK_STAINED_GLASS_PANE));

		return inventory;
	}
	
	@Override
	public Inventory getRewardsContainer(UUID playerUUID)
	{
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> Bukkit.createInventory(null, 9 * 6, "Personal Container"));
	}
	
	@Override
	public Inventory getItemsContainer(UUID playerUUID) 
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> Bukkit.createInventory(null, 9 * 6, "Your Items"));

		inventory.setItem(15, new ItemBuilder(Material.CHEST)
				.named(AQUA + "Items Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy());
		
		InventoryUtils.fillEmptySlots(inventory, createWall(Material.BLACK_STAINED_GLASS_PANE));
		
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
	
	private static String describe(Job job) 
	{
		return ChatColorUtils.colorize(String.format("&6Goal: &f%s &8&l| &6Reward: &f%s", 
				job.getGoal().accept(TextGoalDescriptor.INSTANCE), 
				job.getReward().accept(TextRewardDescriptor.INSTANCE)));
	}
	private static ConversationFactory createConversationFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("stop")
				.withPrefix(context -> Message.GENERAL_PREFIX.toString());
	}
}