package dte.employme.inventories;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryFrameworkUtils.createSquare;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.function.UnaryOperator;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.job.SimpleJob;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.items.ItemBuilder;

//TODO: allow asking for enchanted books
public class GoalCustomizationGUI extends ChestGui
{
	private final ConversationFactory typeConversationFactory;
	private final MessageService messageService;
	private final JobBoard jobBoard;
	private final Reward reward;

	//temp data(items, panes, etc)
	private StaticPane itemPane, optionsPane;
	private GuiItem currentItem;
	private int amount = 1;
	private boolean refundRewardOnClose = true;

	private static final Material NO_ITEM_TYPE = Material.BARRIER;

	public GoalCustomizationGUI(ConversationFactory typeConversationFactory, MessageService messageService, JobBoard jobBoard, Reward reward)
	{
		super(6, "What should the Goal Item be?");

		this.typeConversationFactory = typeConversationFactory;
		this.messageService = messageService;
		this.jobBoard = jobBoard;
		this.reward = reward;

		setOnTopClick(event -> event.setCancelled(true));

		setOnClose(event -> 
		{
			if(this.refundRewardOnClose)
				reward.giveTo((Player) event.getPlayer());
		});

		addPane(createSquare(Priority.LOWEST, 0, 0, 3, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createSquare(Priority.LOWEST, 0, 3, 3, new GuiItem(createWall(Material.LIME_STAINED_GLASS_PANE))));
		addPane(createRectangle(Priority.LOWEST, 3, 0, 6, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createRectangle(Priority.LOW, 5, 1, 3, 4, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createItemPane(Priority.NORMAL));
		addPane(createOptionsPane(Priority.HIGH));
		update();
	}

	public ItemStack getCurrentItem() 
	{
		return new ItemStack(this.currentItem.getItem());
	}

	public Material getType() 
	{
		return this.currentItem.getItem().getType();
	}

	public void setType(Material material)
	{
		if(getType() == NO_ITEM_TYPE) 
			this.optionsPane.addItem(createAmountItem(), 6, 3);
		
		ItemStack updatedItem = new ItemBuilder(this.currentItem.getItem())
				.ofType(material)
				.named(GREEN + "Current Item")
				.withItemFlags(HIDE_ATTRIBUTES)
				.createCopy();
		
		//remove the enchantments that can't be applied to the new material
		updatedItem.getEnchantments().keySet().stream()
		.filter(enchantment -> !enchantment.canEnchantItem(updatedItem))
		.forEach(updatedItem::removeEnchantment);
		
		setEnchantmentsItemVisibility(EnchantmentUtils.isEnchantable(updatedItem));
		updateCurrentItem(item -> updatedItem);
	}

	public void addEnchantment(Enchantment enchantment, int level) 
	{
		updateCurrentItem(item -> 
		{
			item.addEnchantment(enchantment, level);
			return item;
		});
	}

	public void setAmount(int amount) 
	{
		updateCurrentItem(item -> 
		{
			item.setAmount(amount);
			return item;
		});
		
		this.amount = amount;
		this.optionsPane.removeItem(6, 3);
		this.optionsPane.addItem(createAmountItem(), 6, 3);

		update();
	}
	
	public void setRefundRewardOnClose(boolean status) 
	{
		this.refundRewardOnClose = status;
	}

	private void updateCurrentItem(UnaryOperator<ItemStack> update) 
	{
		GuiItem updatedItem = new GuiItem(update.apply(getCurrentItem()));

		this.itemPane.removeItem(1, 1);
		this.itemPane.addItem(this.currentItem = updatedItem, 1, 1);
		
		update();
	}

	private void setEnchantmentsItemVisibility(boolean visible) 
	{
		this.optionsPane.removeItem(6, 2);

		GuiItem updatedItem = visible ? createEnchantmentsItem() : new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE));
		this.optionsPane.addItem(updatedItem, 6, 2);
	}

	private StaticPane createItemPane(Priority priority) 
	{
		StaticPane pane = new StaticPane(0, 0, 6, 9, priority);

		pane.addItem(this.currentItem = new GuiItem(new ItemBuilder(NO_ITEM_TYPE)
				.named(bold(RED) + "Current Goal: None")
				.createCopy()), 1, 1);

		pane.addItem(new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA)
				.named(bold(GREEN) + "Finish")
				.createCopy(), 
				event ->
		{
			Material type = this.currentItem.getItem().getType();

			if(type == NO_ITEM_TYPE)
				return;

			Player player = (Player) event.getWhoClicked();
			closeInventory(player, false);

			Job job = new SimpleJob.Builder()
					.by(player)
					.of(createGoal())
					.thatOffers(this.reward)
					.build();

			this.jobBoard.addJob(job);
		}), 1, 4);

		this.itemPane = pane;

		return pane;
	}

	private StaticPane createOptionsPane(Priority priority) 
	{
		StaticPane pane = new StaticPane(0, 0, 9, 6, priority);

		pane.addItem(new GuiItem(new ItemBuilder(Material.ANVIL)
				.named(GREEN + "Type")
				.withLore(WHITE + "Click to set the type of the goal.")
				.glowing()
				.createCopy(), 
				event -> 
		{
			Player player = (Player) event.getWhoClicked();

			closeInventory(player, false);
			createTypeConversation(player).begin();

		}), 6, 1);

		this.optionsPane = pane;

		return pane;
	}

	private GuiItem createAmountItem() 
	{
		ItemStack item = new ItemBuilder(Material.ARROW)
				.named(GOLD + "Amount: " + bold(WHITE) + this.amount)
				.withLore(
						WHITE + "Left Click to " + GREEN + "Increase" + WHITE + ".",
						WHITE + "Right Click to " + RED + "Decrease" + WHITE + "."
						)
				.glowing()
				.createCopy();

		return new GuiItem(item, event ->
		{
			if(getType() == NO_ITEM_TYPE)
				return;

			if(event.isLeftClick())
				this.amount++;

			else if(event.isRightClick() && this.amount > 1)
				this.amount--;

			setAmount(this.amount);
		});
	}

	private GuiItem createEnchantmentsItem() 
	{
		ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK)
				.named(LIGHT_PURPLE + "Enchantments")
				.withLore(WHITE + "Click to add an enchantment that", WHITE + "the goal must have on it.")
				.glowing()
				.createCopy();

		return new GuiItem(item, event -> 
		{
			if(getType() == NO_ITEM_TYPE)
				return;
			
			closeInventory(event.getWhoClicked(), false);
			new GoalEnchantmentSelectionGUI(this.messageService, this).show(event.getWhoClicked());
		});
	}

	private Conversation createTypeConversation(Player employer) 
	{
		Conversation conversation = this.typeConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("goal inventory", this);

		return conversation;
	}

	private void closeInventory(HumanEntity human, boolean refundReward) 
	{
		human.closeInventory();
		setRefundRewardOnClose(refundReward);
	}
	
	private ItemStack createGoal() 
	{
		return new ItemBuilder(getType())
				.amounted(this.amount)
				.withEnchantments(getCurrentItem().getEnchantments())
				.createCopy();
	}
}