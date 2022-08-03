package dte.employme.inventories;

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
import static dte.employme.messages.MessageKey.GUI_ITEM_PALETTE_TITLE;
import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.base.Predicates;

import dte.employme.board.JobBoard;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.job.Job;
import dte.employme.rewards.Reward;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class GoalCustomizationGUI extends ChestGui
{
	private final MessageService messageService;
	private final JobSubscriptionService jobSubscriptionService;
	private final JobBoard jobBoard;
	private final Reward reward;

	//temp data(items, panes, etc)
	private StaticPane itemPane, optionsPane;
	private GuiItem currentItem;
	private int amount = 1;
	private boolean refundRewardOnClose = true;

	private static final Material NO_ITEM_TYPE = Material.BARRIER;

	public GoalCustomizationGUI(MessageService messageService, JobSubscriptionService jobSubscriptionService, JobBoard jobBoard, Reward reward)
	{
		super(6, messageService.getMessage(GUI_GOAL_CUSTOMIZATION_TITLE).first());
		
		this.messageService = messageService;
		this.jobSubscriptionService = jobSubscriptionService;
		this.jobBoard = jobBoard;
		this.reward = reward;
		
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
		return new ItemStack(this.currentItem.getItem());
	}

	public Material getType() 
	{
		return this.currentItem.getItem().getType();
	}
	
	public int getAmount() 
	{
		return this.amount;
	}

	public void setType(Material material)
	{
		if(getType() == NO_ITEM_TYPE)
			this.optionsPane.addItem(createAmountItem(), 6, 2);
		
		updateCurrentItem(item -> 
		{
			Map<Enchantment, Integer> enchantments = EnchantmentUtils.getAllEnchantments(item);
			
			ItemStack updatedItem = new ItemBuilder(item)
					.ofType(material) //set the new material
					.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME).first())
					.withItemFlags(HIDE_ATTRIBUTES)
					.createCopy();
			
			//remove all enchantments, and return only the valid ones
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
		this.optionsPane.addItem(createAmountItem(), 6, 2);
		
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
	
	
	
	/*
	 * Panes
	 */
	private Pane createItemPane() 
	{
		StaticPane pane = new StaticPane(0, 0, 6, 9, Priority.NORMAL);

		pane.addItem(this.currentItem = createNoItemIcon(), 1, 1);
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
					Material type = this.currentItem.getItem().getType();

					//the user didn't select an item
					if(type == NO_ITEM_TYPE)
						return;

					//the goal is a non-enchanted enchanted book lmfao
					if(type == Material.ENCHANTED_BOOK && getEnchantments(getCurrentItem()).isEmpty())
						return;

					Player player = (Player) event.getWhoClicked();
					closeWithoutRefund(player);

					this.jobBoard.addJob(new Job(player, createGoal(), this.reward));
				})
				.build();
	}

	private GuiItem createNoItemIcon() 
	{
		return new GuiItem(new ItemBuilder(NO_ITEM_TYPE)
				.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME).first())
				.createCopy());
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
					if(getType() == NO_ITEM_TYPE)
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
						.named(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME).inject(GOAL_AMOUNT, this.amount).first())
						.withLore(this.messageService.getMessage(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE).toArray())
						.glowing()
						.createCopy())
				.whenClicked(event -> 
				{
					HumanEntity player = event.getWhoClicked();

					closeWithoutRefund(player);
					new GoalAmountGUI(this, this.messageService).show(player);
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
					new TypeItemPaletteGUI(this.messageService, this.jobSubscriptionService, this, this.reward).show(player);
				})
				.build();
	}
	
	private void updateCurrentItem(UnaryOperator<ItemStack> update) 
	{
		GuiItem updatedItem = new GuiItem(update.apply(getCurrentItem()));

		this.itemPane.addItem(this.currentItem = updatedItem, 1, 1);
		
		update();
	}

	private void setEnchantmentsItemVisibility(boolean visible) 
	{
		GuiItem updatedItem = visible ? createEnchantmentsItem() : new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE));
		
		this.optionsPane.addItem(updatedItem, 6, 3);
	}



	private class TypeItemPaletteGUI extends ItemPaletteGUI
	{
		private boolean showGoalCustomizationGUIOnClose = true;

		public TypeItemPaletteGUI(MessageService messageService, JobSubscriptionService jobSubscriptionService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
		{
			super(messageService.getMessage(GUI_ITEM_PALETTE_TITLE).first(),
					messageService,

					item -> new GuiItemBuilder()
					.forItem(new ItemStack(item))
					.whenClicked(event -> 
					{
						goalCustomizationGUI.setType(item);
						event.getWhoClicked().closeInventory();
					})
					.build(),
					
					Predicates.alwaysTrue(),

					Conversations.createFactory(messageService)
					.withFirstPrompt(new JobGoalPrompt(messageService, messageService.getMessage(ITEM_GOAL_FORMAT_QUESTION).first()))
					.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
					.addConversationAbandonedListener(Conversations.refundRewardIfAbandoned(messageService, JOB_SUCCESSFULLY_CANCELLED))
					.addConversationAbandonedListener(event -> 
					{
						if(!event.gracefulExit())
							return;

						//re-open the goal customization gui
						Player player = (Player) event.getContext().getForWhom();
						Material material = (Material) event.getContext().getSessionData("material");

						goalCustomizationGUI.setRefundRewardOnClose(true);
						goalCustomizationGUI.setType(material);
						goalCustomizationGUI.show(player);
					}));


			setEnglishItemClickHandler(event -> this.showGoalCustomizationGUIOnClose = false);

			setOnClose(event -> 
			{
				if(!this.showGoalCustomizationGUIOnClose)
					return;

				goalCustomizationGUI.setRefundRewardOnClose(true);
				goalCustomizationGUI.show(event.getPlayer());
			});
		}
	}
}