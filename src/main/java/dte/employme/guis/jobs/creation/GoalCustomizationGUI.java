package dte.employme.guis.jobs.creation;

import static dte.employme.conversations.Conversations.refundRewardIfAbandoned;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_TITLE;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.messages.Placeholders.GOAL_AMOUNT;
import static dte.employme.utils.EnchantmentUtils.canEnchantItem;
import static dte.employme.utils.EnchantmentUtils.enchant;
import static dte.employme.utils.EnchantmentUtils.getEnchantments;
import static dte.employme.utils.EnchantmentUtils.isEnchantable;
import static dte.employme.utils.EnchantmentUtils.removeEnchantment;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createSquare;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import dte.employme.board.JobBoard;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.GoalAmountPrompt;
import dte.employme.items.providers.ItemProvider;
import dte.employme.items.providers.VanillaProvider;
import dte.employme.job.Job;
import dte.employme.rewards.Reward;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;

public class GoalCustomizationGUI extends ChestGui
{
	private final MessageService messageService;
	private final JobSubscriptionService jobSubscriptionService;
	private final JobService jobService;
	private final JobBoard jobBoard;
	private final Reward reward;
	private ItemStack currentItem;
	private ItemProvider provider;
	private boolean refundRewardOnClose = true;
	private StaticPane itemPane, optionsPane;
	
	private static final Material NO_ITEM_TYPE = Material.BARRIER;

	public GoalCustomizationGUI(MessageService messageService, JobSubscriptionService jobSubscriptionService, JobService jobService, JobBoard jobBoard, Reward reward)
	{
		super(6, messageService.getMessage(GUI_GOAL_CUSTOMIZATION_TITLE).first());
		
		this.messageService = messageService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.jobService = jobService;
		this.jobBoard = jobBoard;
		this.reward = reward;
		this.currentItem = createNoItemIcon();
		
		setOnTopClick(event -> event.setCancelled(true));
		
		setOnClose(event -> 
		{
			if(this.refundRewardOnClose) 
			{
				Player player = (Player) event.getPlayer();
				
				reward.giveTo(player);
				this.messageService.getMessage(JOB_SUCCESSFULLY_CANCELLED).sendTo(player);
			}
		});

		addPane(createSquare(Priority.LOWEST, 0, 0, 3, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createSquare(Priority.LOWEST, 0, 3, 3, new GuiItem(createWall(Material.LIME_STAINED_GLASS_PANE))));
		addPane(createRectangle(Priority.LOWEST, 3, 0, 6, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createRectangle(Priority.LOW, 5, 1, 3, 4, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createItemPane());
		addPane(createOptionsPane());
		update();
	}

	public ItemStack getCurrentItem() 
	{
		return new ItemStack(this.currentItem);
	}

	public void setCurrentItem(ItemStack item, ItemProvider provider) 
	{
		//show the amount item after initially setting the item
		if(this.currentItem.getType() == NO_ITEM_TYPE)
			this.optionsPane.addItem(createAmountItem(), 6, 2);

		this.currentItem = item;
		this.provider = provider;
		this.itemPane.addItem(new GuiItem(item), 1, 1);

		//if the item is custom, its possible enchantments, etc(except for amount) cannot be modified
		setEnchantmentsItemVisibility(provider instanceof VanillaProvider ? isEnchantable(item) : false);

		update();
	}

	public void setType(Material material)
	{
		ItemStack item = new ItemBuilder(material)
				.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME).first())
				.withItemFlags(HIDE_ATTRIBUTES)
				.createCopy();
		
		//remove all enchantments, and return only the valid ones
		Map<Enchantment, Integer> enchantments = EnchantmentUtils.getAllEnchantments(item);

		enchantments.keySet().stream()
		.peek(enchantment -> removeEnchantment(item, enchantment))
		.filter(enchantment -> canEnchantItem(enchantment, item))
		.forEach(enchantment -> enchant(item, enchantment, enchantments.get(enchantment)));

		setCurrentItem(item, VanillaProvider.INSTANCE);
		update();
	}

	public void addEnchantment(Enchantment enchantment, int level) 
	{
		enchant(this.currentItem, enchantment, level);
		update();
	}

	public void setAmount(int amount) 
	{
		this.optionsPane.addItem(createAmountItem(), 6, 2);
		this.currentItem.setAmount(amount);
		update();
	}
	
	public void setRefundRewardOnClose(boolean status) 
	{
		this.refundRewardOnClose = status;
	}
	
	private void closeWithoutRefund(HumanEntity human) 
	{
		setRefundRewardOnClose(false);
		human.closeInventory();
	}
	
	private void setEnchantmentsItemVisibility(boolean visible) 
	{
		GuiItem updatedItem = visible ? createEnchantmentsItem() : new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE));

		this.optionsPane.addItem(updatedItem, 6, 3);
		update();
	}

	
	
	/*
	 * Panes
	 */
	private Pane createItemPane() 
	{
		StaticPane pane = new StaticPane(0, 0, 6, 9, Priority.NORMAL);
		pane.addItem(new GuiItem(this.currentItem), 1, 1);
		pane.addItem(createFinishItem(), 1, 4);

		this.itemPane = pane;

		return pane;
	}

	private Pane createOptionsPane() 
	{
		StaticPane pane = new StaticPane(0, 0, 9, 6, Priority.HIGH);
		pane.addItem(createTypeChoosingItem(), 6, 1);

		this.optionsPane = pane;

		return pane;
	}

	
	
	/*
	 * Items
	 */
	private GuiItem createFinishItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.GREEN_TERRACOTTA)
						.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME).first())
						.createCopy())
				.whenClicked(event -> 
				{
					Material type = this.currentItem.getType();
					
					//the user didn't select an item
					if(type == NO_ITEM_TYPE)
						return;

					//the goal is a non-enchanted enchanted book lmfao
					if(type == Material.ENCHANTED_BOOK && getEnchantments(getCurrentItem()).isEmpty())
						return;

					Player player = (Player) event.getWhoClicked();
					closeWithoutRefund(player);
					
					this.jobBoard.addJob(new Job(player, createFinalItem(), this.provider, this.reward));
				})
				.build();
	}

	private ItemStack createNoItemIcon() 
	{
		return new ItemBuilder(NO_ITEM_TYPE)
				.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME).first())
				.createCopy();
	}

	private GuiItem createEnchantmentsItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.ENCHANTED_BOOK)
						.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME).first())
						.withLore(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE).toArray())
						.glowing()
						.createCopy())
				.whenClicked(event -> 
				{
					if(this.currentItem.getType() == NO_ITEM_TYPE)
						return;

					closeWithoutRefund(event.getWhoClicked());
					new GoalEnchantmentSelectionGUI(this.messageService, this, this.reward).show(event.getWhoClicked());
				})
				.build();
	}

	private GuiItem createAmountItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.ARROW)
						.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME).inject(GOAL_AMOUNT, this.currentItem.getAmount()).first())
						.withLore(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE).toArray())
						.glowing()
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();

					closeWithoutRefund(player);
					
					Conversations.createFactory(this.messageService)
					.withFirstPrompt(new GoalAmountPrompt(this.messageService))
					.addConversationAbandonedListener(refundRewardIfAbandoned(this.messageService, JOB_SUCCESSFULLY_CANCELLED))
					.addConversationAbandonedListener(abandonEvent -> 
					{
						if(!abandonEvent.gracefulExit())
							return;
						
						setRefundRewardOnClose(true);
						setAmount((int) abandonEvent.getContext().getSessionData("amount"));
						show(player);
					})
					.buildConversation(player)
					.begin();
				})
				.build();
	}

	private GuiItem createTypeChoosingItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.ANVIL)
						.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME).first())
						.withLore(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE).toArray())
						.glowing()
						.createCopy())
				.whenClicked(event -> 
				{
					HumanEntity player = event.getWhoClicked();
					closeWithoutRefund(player);

					if(event.getClick().isLeftClick()) 
						new ItemPaletteGoalGUI(player.getWorld(), jobService, this.messageService, this.jobSubscriptionService, this, this.reward).show(player);

					else if(event.getClick().isRightClick()) 
						new CustomItemSelectionGUI(this.messageService, this.jobSubscriptionService, this, this.reward).show(player);
				})
				.build();
	}

	private ItemStack createFinalItem() 
	{
		if(!(this.provider instanceof VanillaProvider))
			return this.currentItem;

		return new ItemBuilder(this.currentItem.getType())
				.amounted(this.currentItem.getAmount())
				.withEnchantments(getEnchantments(this.currentItem))
				.createCopy();
	}

}