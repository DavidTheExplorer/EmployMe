package dte.employme.job.service;

import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.utils.items.ItemBuilder;
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
	private final Map<UUID, Inventory> itemsContainers = new HashMap<>(), rewardsContainers = new HashMap<>();
	
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
		
		job.getReward().giveTo(completer);
		
		ItemStack goal = job.getGoal();
		InventoryUtils.remove(completer.getInventory(), goal);
		getItemsContainer(job.getEmployer().getUniqueId()).addItem(goal);
		
		//message the completer
		Message.sendGeneralMessage(completer, (job.getReward() instanceof ItemsReward ? Message.ITEMS_JOB_COMPLETED : Message.JOB_COMPLETED));
		
		//notify the employer
		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> employer.spigot().sendMessage(new ComponentBuilder(Message.GENERAL_PREFIX + Message.PLAYER_COMPLETED_YOUR_JOB.inject(completer.getName()))
				.event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job))))
				.create()));
	}
	
	@Override
	public boolean hasFinished(Job job, Player player)
	{
		ItemStack goalItem = job.getGoal();
		
		return player.getInventory().containsAtLeast(goalItem, goalItem.getAmount());
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
		return this.rewardsContainers.computeIfAbsent(playerUUID, u -> createContainerInventory("Claim your Rewards:", 
				"This is where Reward Items are stored", 
				"after you complete a job that pays them."));
	}
	
	@Override
	public Inventory getItemsContainer(UUID playerUUID)
	{
		return this.itemsContainers.computeIfAbsent(playerUUID, u -> createContainerInventory("Claim your Items:",
				"When someone completes one of your jobs,", 
				"The items they got for you are stored here."));
	}
	
	@Override
	public Conversation buildMoneyJobConversation(Player employer)
	{
		return this.moneyJobConversationFactory.buildConversation(employer);
	}
	
	@Override
	public Conversation buildItemsJobConversation(Player employer, Collection<ItemStack> offeredItems)
	{
		Conversation conversation = this.itemsJobConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("reward", new ItemsReward(offeredItems));

		return conversation;
	}
	
	private static String describe(Job job) 
	{
		return ChatColorUtils.colorize(String.format("&6Goal: &f%s &8&l| &6Reward: &f%s", 
				"Get " + ItemStackUtils.describe(job.getGoal()), 
				job.getReward().accept(TextRewardDescriptor.INSTANCE)));
	}
	
	private Inventory createCreationInventory()
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Create a new Job");

		inventory.setItem(11, new ItemBuilder(Material.GOLD_INGOT)
				.named(GOLD + "Money Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay a certain amount of money.")
				.createCopy());

		inventory.setItem(15, new ItemBuilder(Material.CHEST)
				.named(AQUA + "Items Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy());
		
		InventoryUtils.fillEmptySlots(inventory, createWall(Material.BLACK_STAINED_GLASS_PANE));
		
		return inventory;
	}

	private static ConversationFactory createConversationFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("stop")
				.withPrefix(context -> Message.GENERAL_PREFIX.toString());
	}
	
	private static Inventory createContainerInventory(String title, String... bookDescription) 
	{
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, title);
		
		inventory.setItem(43, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(44, createWall(Material.GRAY_STAINED_GLASS_PANE));
		inventory.setItem(52, createWall(Material.GRAY_STAINED_GLASS_PANE));
		
		inventory.setItem(53, new ItemBuilder(Material.BOOK)
				.named(GREEN + "Help")
				.withLore(Arrays.stream(bookDescription).map(line -> WHITE + line).toArray(String[]::new))
				.createCopy());
		
		return inventory;
	}
}