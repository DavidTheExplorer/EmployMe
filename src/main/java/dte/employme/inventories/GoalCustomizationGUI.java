package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME;
import static dte.employme.messages.Placeholders.GOAL_AMOUNT;
import static dte.employme.utils.EnchantmentUtils.canEnchantItem;
import static dte.employme.utils.EnchantmentUtils.enchant;
import static dte.employme.utils.EnchantmentUtils.getEnchantments;
import static dte.employme.utils.EnchantmentUtils.isEnchantable;
import static dte.employme.utils.EnchantmentUtils.removeEnchantment;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryFrameworkUtils.createSquare;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.Map;
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
import dte.employme.job.prompts.JobGoalPrompt;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.Conversations;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

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

	public GoalCustomizationGUI(MessageService messageService, JobBoard jobBoard, Reward reward)
	{
		super(6, messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_TITLE).first());
		
		this.messageService = messageService;
		this.jobBoard = jobBoard;
		this.reward = reward;
		this.typeConversationFactory = Conversations.createFactory(messageService)
				.withLocalEcho(false)
				.withFirstPrompt(new JobGoalPrompt(messageService))
				.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
				.addConversationAbandonedListener(Conversations.REFUND_REWARD_IF_ABANDONED)
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;
					
					//re-open the goal customization gui
					Player player = (Player) event.getContext().getForWhom();
					Material material = (Material) event.getContext().getSessionData("material");
					GoalCustomizationGUI goalCustomizationGUI = (GoalCustomizationGUI) event.getContext().getSessionData("goal inventory");

					goalCustomizationGUI.setRefundRewardOnClose(true);
					goalCustomizationGUI.setType(material);
					goalCustomizationGUI.show(player);
				});
	
		
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
		
		updateCurrentItem(item -> 
		{
			Map<Enchantment, Integer> enchantments = EnchantmentUtils.getAllEnchantments(item);
			
			ItemStack updatedItem = new ItemBuilder(item)
					.ofType(material) //set the new material
					.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME).first())
					.withItemFlags(HIDE_ATTRIBUTES)
					.createCopy();
			
			//remove the invalid enchantments, and put the valid ones in their proper places(stored/regular)
			enchantments.keySet().stream()
			.peek(enchantment -> removeEnchantment(updatedItem, enchantment))
			.filter(enchantment -> canEnchantItem(enchantment, updatedItem))
			.forEach(enchantment -> enchant(updatedItem, enchantment, enchantments.get(enchantment)));
			
			return updatedItem;
		});
		
		setEnchantmentsItemVisibility(isEnchantable(getCurrentItem()));
	}

	public void addEnchantment(Enchantment enchantment, int level) 
	{
		updateCurrentItem(item -> 
		{
			enchant(item, enchantment, level);
			return item;
		});
	}

	public void setAmount(int amount) 
	{
		this.amount = amount;
		this.optionsPane.removeItem(6, 3);
		this.optionsPane.addItem(createAmountItem(), 6, 3);
		
		updateCurrentItem(item -> 
		{
			item.setAmount(amount);
			return item;
		});

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
		GuiItem updatedItem = visible ? createEnchantmentsItem() : new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE));
		
		this.optionsPane.removeItem(6, 2);
		this.optionsPane.addItem(updatedItem, 6, 2);
	}

	private StaticPane createItemPane(Priority priority) 
	{
		StaticPane pane = new StaticPane(0, 0, 6, 9, priority);

		pane.addItem(this.currentItem = new GuiItem(new ItemBuilder(NO_ITEM_TYPE)
				.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME).first())
				.createCopy()), 1, 1);

		pane.addItem(new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA)
				.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME).first())
				.createCopy(), 
				event ->
		{
			Material type = this.currentItem.getItem().getType();
			
			//the user didn't select an item
			if(type == NO_ITEM_TYPE)
				return;
			
			//the goal is a non-enchanted enchanted book lmfao
			if(type == Material.ENCHANTED_BOOK && getEnchantments(getCurrentItem()).isEmpty())
				return;

			Player player = (Player) event.getWhoClicked();
			closeWithoutRefund(player);

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
				.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME).first())
				.withLore(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE).toArray())
				.glowing()
				.createCopy(), 
				event -> 
		{
			Player player = (Player) event.getWhoClicked();

			closeWithoutRefund(player);
			createTypeConversation(player).begin();

		}), 6, 1);

		this.optionsPane = pane;

		return pane;
	}

	private GuiItem createAmountItem() 
	{
		ItemStack item = new ItemBuilder(Material.ARROW)
				.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME).inject(GOAL_AMOUNT, String.valueOf(this.amount)).first())
				.withLore(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE).toArray())
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
				.named(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME).first())
				.withLore(this.messageService.getMessage(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE).toArray())
				.glowing()
				.createCopy();

		return new GuiItem(item, event -> 
		{
			if(getType() == NO_ITEM_TYPE)
				return;
			
			closeWithoutRefund(event.getWhoClicked());
			new GoalEnchantmentSelectionGUI(this.messageService, this).show(event.getWhoClicked());
		});
	}

	//TODO: remove
	private Conversation createTypeConversation(Player employer) 
	{
		Conversation conversation = this.typeConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("goal inventory", this);

		return conversation;
	}

	private void closeWithoutRefund(HumanEntity human) 
	{
		setRefundRewardOnClose(false);
		human.closeInventory();
	}

	private ItemStack createGoal()
	{
		return new ItemBuilder(getType())
				.amounted(this.amount)
				.withEnchantments(getEnchantments(getCurrentItem()))
				.createCopy();
	}
}