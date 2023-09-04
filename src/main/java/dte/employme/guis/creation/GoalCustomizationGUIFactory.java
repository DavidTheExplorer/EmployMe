package dte.employme.guis.creation;

import static dte.employme.conversations.Conversations.createRewardRefundListener;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.EnchantmentUtils.canEnchantItem;
import static dte.employme.utils.EnchantmentUtils.enchant;
import static dte.employme.utils.EnchantmentUtils.getEnchantments;
import static dte.employme.utils.EnchantmentUtils.isEnchantable;
import static dte.employme.utils.EnchantmentUtils.removeEnchantment;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createSquare;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.GoalAmountPrompt;
import dte.employme.items.providers.ItemProvider;
import dte.employme.items.providers.VanillaProvider;
import dte.employme.job.Job;
import dte.employme.job.creation.JobCreationContext;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.RespectingChestGui;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class GoalCustomizationGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;
	private final GoalSelectionGUIFactory goalSelectionGUIFactory;
	private final GoalEnchantmentSelectionGUIFactory goalEnchantmentSelectionGUIFactory;
	private final CustomItemSelectionGUIFactory customItemSelectionGUIFactory;

	private static final Material NO_ITEM = Material.BARRIER;

	public GoalCustomizationGUIFactory(GuiConfig config, MessageService messageService, GoalSelectionGUIFactory goalSelectionGUIFactory, GoalEnchantmentSelectionGUIFactory goalEnchantmentSelectionGUIFactory, CustomItemSelectionGUIFactory customItemSelectionGUIFactory) 
	{
		this.config = config;
		this.messageService = messageService;
		this.goalSelectionGUIFactory = goalSelectionGUIFactory;
		this.goalEnchantmentSelectionGUIFactory = goalEnchantmentSelectionGUIFactory;
		this.customItemSelectionGUIFactory = customItemSelectionGUIFactory;
	}

	public GoalCustomizationGUI create(Player viewer, JobCreationContext creationContext) 
	{
		GoalCustomizationGUI gui = new GoalCustomizationGUI(6, this.config.getTitle());

		//add panes
		gui.init(
				parseItemPane(gui, creationContext), 
				parseOptionsPane(viewer, gui, creationContext), 
				parseAmountItem(viewer, gui, creationContext), 
				parseEnchantmentsItem(viewer, gui, creationContext),
				parseNoGoalItem().getItem());
		
		gui.addPane(createSquare(Priority.LOWEST, 0, 0, 3, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		gui.addPane(createSquare(Priority.LOWEST, 0, 3, 3, new GuiItem(createWall(Material.LIME_STAINED_GLASS_PANE))));
		gui.addPane(createRectangle(Priority.LOWEST, 3, 0, 6, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		gui.addPane(createRectangle(Priority.LOW, 5, 1, 3, 4, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));

		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		gui.setOnClose(createRefundListener(viewer, gui, creationContext));

		return gui;
	}



	/*
	 * Panes
	 */
	private OutlinePane parseItemPane(GoalCustomizationGUI gui, JobCreationContext context) 
	{
		OutlinePane pane = new OutlinePane(1, 1, 9, 6, Priority.NORMAL);
		pane.setOrientation(Orientation.VERTICAL);
		pane.setGap(2);
		pane.addItem(parseNoGoalItem());
		pane.addItem(parseFinishItem(gui, context));

		return pane;
	}

	private OutlinePane parseOptionsPane(Player viewer, GoalCustomizationGUI gui, JobCreationContext context) 
	{
		OutlinePane pane = new OutlinePane(6, 1, 1, 4, Priority.HIGH);
		pane.addItem(parseTypeChoosingItem(viewer, gui, context));

		return pane;
	}



	/*
	 * Items
	 */
	private GuiItem parseNoGoalItem() 
	{
		return new GuiItem(new ItemBuilder(NO_ITEM)
				.named(this.config.getText("no-current-goal").first())
				.createCopy());
	}
	
	private GuiItem parseFinishItem(GoalCustomizationGUI gui, JobCreationContext context) 
	{
		return this.config.parseGuiItem("finish-item")
				.whenClicked(event -> 
				{
					Material type = gui.getCurrentItem().getType();

					//the user didn't select an item
					if(type == NO_ITEM)
						return;

					//the goal is a non-enchanted enchanted book lmfao
					if(type == Material.ENCHANTED_BOOK && getEnchantments(gui.getCurrentItem()).isEmpty())
						return;

					Player player = (Player) event.getWhoClicked();
					gui.closeWithoutRefund(player);

					context.getDestinationBoard().addJob(new Job(player, createFinalItem(gui), gui.getCurrentItemProvider(), context.getReward()));
				})
				.build();
	}

	private GuiItem parseEnchantmentsItem(Player viewer, GoalCustomizationGUI gui, JobCreationContext context) 
	{
		ItemStack item = new ItemBuilder(this.config.parseItem("items.enchantments"))
				.glowing()
				.createCopy();

		return new GuiItemBuilder()
				.forItem(item)
				.whenClicked(event -> 
				{
					if(gui.getCurrentItem().getType() == NO_ITEM)
						return;

					gui.closeWithoutRefund(viewer);

					RespectingChestGui enchantmentSelectionGUI = this.goalEnchantmentSelectionGUIFactory.create(viewer, context, EnchantmentUtils.getRemainingEnchantments(gui.getCurrentItem()), gui);
					enchantmentSelectionGUI.openOnClose(gui);
					
					enchantmentSelectionGUI.addCloseListener(closeEvent -> 
					{
						if(!enchantmentSelectionGUI.hasParent())
							return;

						gui.refundRewardOnClose(true);
						gui.show(viewer);
					});

					enchantmentSelectionGUI.show(viewer);
				})
				.build();
	}

	private GuiItem parseAmountItem(Player viewer, GoalCustomizationGUI gui, JobCreationContext context) 
	{
		GuiItem guiItem = this.config.parseGuiItem("amount")
				.whenClicked(event -> 
				{
					gui.closeWithoutRefund(viewer);

					Conversations.createFactory(this.messageService)
					.withFirstPrompt(new GoalAmountPrompt(this.messageService))
					.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", context.getReward()).build())
					.addConversationAbandonedListener(createRewardRefundListener(this.messageService, JOB_SUCCESSFULLY_CANCELLED))
					.addConversationAbandonedListener(abandonEvent -> 
					{
						if(!abandonEvent.gracefulExit())
							return;

						gui.refundRewardOnClose(true);
						gui.setAmount((int) abandonEvent.getContext().getSessionData("amount"));
						gui.show(viewer);
					})
					.buildConversation(viewer)
					.begin();
				})
				.build();

		//add glow
		ItemStack glowingItem = new ItemBuilder(guiItem.getItem())
				.glowing()
				.createCopy();

		guiItem.setItem(glowingItem);

		return guiItem;
	}

	private GuiItem parseTypeChoosingItem(Player viewer, GoalCustomizationGUI gui, JobCreationContext context) 
	{
		GuiItem guiItem = this.config.parseGuiItem("type-chooser")
				.whenClicked(event -> 
				{
					if(event.getClick().isLeftClick()) 
					{
						gui.closeWithoutRefund(viewer);

						this.goalSelectionGUIFactory.create(viewer, gui, context).show(viewer);
					}
					else if(event.getClick().isRightClick()) 
					{
						if(ItemProvider.getAvailable().isEmpty())
							return;

						gui.closeWithoutRefund(viewer);
						this.customItemSelectionGUIFactory.create(viewer, gui).show(viewer);
					}
				})
				.build();


		//add glow + custom item support line
		List<String> lore = guiItem.getItem().getItemMeta().getLore();

		if(!ItemProvider.getAvailable().isEmpty())
			lore.addAll(this.config.getText("custom-item-support").toList());

		ItemStack finalItem = new ItemBuilder(guiItem.getItem())
				.glowing()
				.withLore(lore.toArray(new String[0]))
				.createCopy();

		guiItem.setItem(finalItem);

		return guiItem;
	}

	private ItemStack createFinalItem(GoalCustomizationGUI gui) 
	{
		ItemStack currentItem = gui.getCurrentItem();
		
		//if the item is NOT vanilla, don't touch it
		if(!(gui.getCurrentItemProvider() instanceof VanillaProvider))
			return currentItem;

		return new ItemBuilder(currentItem.getType())
				.amounted(currentItem.getAmount())
				.withEnchantments(getEnchantments(currentItem))
				.createCopy();
	}

	private Consumer<InventoryCloseEvent> createRefundListener(Player viewer, GoalCustomizationGUI gui, JobCreationContext context)
	{
		return event -> 
		{
			if(!gui.doesRefundRewardOnClose())
				return;
			
			context.getReward().giveTo(viewer);
			this.messageService.loadMessage(JOB_SUCCESSFULLY_CANCELLED).sendTo(viewer);
		};
	}



	public class GoalCustomizationGUI extends ChestGui
	{
		//current item
		private ItemStack currentItem;
		private ItemProvider currentItemProvider;
		
		//gui objects
		private OutlinePane itemPane, optionsPane;
		private GuiItem amountItem, enchantmentsItem;
		
		//settings
		private boolean refundRewardOnClose = true;

		public GoalCustomizationGUI(int rows, String title)
		{
			super(rows, title);
		}

		void init(OutlinePane itemPane, OutlinePane optionsPane, GuiItem amountItem, GuiItem enchantmentsItem, ItemStack noGoalIcon) 
		{
			this.amountItem = amountItem;
			this.enchantmentsItem = enchantmentsItem;
			this.currentItem = noGoalIcon;
			this.itemPane = itemPane;
			this.optionsPane = optionsPane;
			
			addPane(itemPane);
			addPane(optionsPane);
		}

		public ItemStack getCurrentItem() 
		{
			return new ItemStack(this.currentItem);
		}

		public ItemProvider getCurrentItemProvider()
		{
			return this.currentItemProvider;
		}

		public boolean doesRefundRewardOnClose() 
		{
			return this.refundRewardOnClose;
		}

		public void refundRewardOnClose(boolean status) 
		{
			this.refundRewardOnClose = status;
		}

		public void setCurrentItem(ItemStack item, ItemProvider itemProvider)
		{
			//show the amount item after initially setting the item
			if(this.currentItem.getType() == NO_ITEM) 
				this.optionsPane.addItem(this.amountItem);

			//keep the item's amount
			item.setAmount(this.currentItem.getAmount());

			this.currentItem = item;
			this.currentItemProvider = itemProvider;

			this.itemPane.removeItem(this.itemPane.getItems().get(0));
			this.itemPane.insertItem(new GuiItem(item), 0);

			//if the item is custom, its possible enchantments, etc(except for amount) cannot be modified
			updateEnchantmentsItemVisibility();

			update();
		}

		public void setType(Material material)
		{
			ItemStack item = new ItemBuilder(material)
					.named(config.getText("current-goal").first())
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
			this.optionsPane.removeItem(this.optionsPane.getItems().get(1));
			this.optionsPane.insertItem(this.amountItem, 1);
			this.currentItem.setAmount(amount);
			update();
		}

		private void updateEnchantmentsItemVisibility() 
		{
			boolean visible = this.currentItemProvider instanceof VanillaProvider ? isEnchantable(this.currentItem) : false;

			GuiItem updatedItem = visible ? this.enchantmentsItem : new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE));

			if(this.optionsPane.getItems().size() >= 3)
				this.optionsPane.removeItem(this.optionsPane.getItems().get(2));

			this.optionsPane.insertItem(updatedItem, 2);
			update();
		}

		public void closeWithoutRefund(HumanEntity human) 
		{
			refundRewardOnClose(false);
			human.closeInventory();
		}
	}
}