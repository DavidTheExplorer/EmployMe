package dte.employme.guis.jobs.creation;

import static dte.employme.conversations.Conversations.refundReward;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_GOAL_ENCHANTMENT_SELECTION_TITLE;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static java.util.Comparator.comparing;
import static org.bukkit.ChatColor.GREEN;

import java.util.Comparator;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.EnchantmentLevelPrompt;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class GoalEnchantmentSelectionGUI extends ChestGui
{
	private final MessageService messageService;
	private final GoalCustomizationGUI goalCustomizationGUI;
	private final Reward reward;

	private PaginatedPane enchantmentsPane;
	private boolean showCustomizationGUIOnClose = true;

	private static final Comparator<Enchantment> ORDER_BY_NAME = comparing(enchantment -> enchantment.getKey().getKey());

	public GoalEnchantmentSelectionGUI(MessageService messageService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
	{
		super(6, messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_TITLE).first());
		
		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;
		this.reward = reward;

		setOnClose(event -> 
		{
			if(!this.showCustomizationGUIOnClose)
				return;
			
			goalCustomizationGUI.setRefundRewardOnClose(true);
			goalCustomizationGUI.show(event.getPlayer());
		});

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(this.enchantmentsPane = getEnchantmentsPane());
		addPane(getPanelPane());
		update();
	}

	private PaginatedPane getEnchantmentsPane() 
	{
		PaginatedPane pages = new PaginatedPane(0, 0, 9, 5, Priority.LOW);
		pages.addPane(0, new OutlinePane(0, 0, 9, 5, Priority.NORMAL));

		EnchantmentUtils.getRemainingEnchantments(this.goalCustomizationGUI.getCurrentItem()).stream()
		.sorted(ORDER_BY_NAME)
		.map(this::createEnchantedBook)
		.forEach(book -> InventoryFrameworkUtils.addItem(book, pages, this));
		
		return pages;
	}

	private Pane getPanelPane() 
	{
		OutlinePane pane = new OutlinePane(2, 5, 9, 1);
		pane.setGap(3);

		pane.addItem(new GuiItemBuilder()
				.forItem(backButtonBuilder()
						.named(this.messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(backButtonListener(this, this.enchantmentsPane))
				.build());

		pane.addItem(new GuiItemBuilder()
				.forItem(nextButtonBuilder()
						.named(this.messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(nextButtonListener(this, this.enchantmentsPane))
				.build());

		return pane;
	}

	private GuiItem createEnchantedBook(Enchantment enchantment) 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.ENCHANTED_BOOK)
						.named(GREEN + EnchantmentUtils.getDisplayName(enchantment))
						.withLore(this.messageService.loadMessage(GUI_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					this.showCustomizationGUIOnClose = false;

					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					beginLevelConversation(player, enchantment);
				})
				.build();
	}

	private void beginLevelConversation(Player player, Enchantment enchantment) 
	{
		Conversations.createFactory(this.messageService)
		.withFirstPrompt(new EnchantmentLevelPrompt(enchantment, this.messageService))
		.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", this.reward).build())
		.addConversationAbandonedListener(refundReward(this.messageService, JOB_SUCCESSFULLY_CANCELLED))
		.addConversationAbandonedListener(abandonedEvent -> 
		{
			if(!abandonedEvent.gracefulExit())
				return;

			int level = (int) abandonedEvent.getContext().getSessionData("level");

			this.goalCustomizationGUI.addEnchantment(enchantment, level);
			this.goalCustomizationGUI.setRefundRewardOnClose(true);
			this.goalCustomizationGUI.show(player);
		})
		.buildConversation(player)
		.begin();
	}
}