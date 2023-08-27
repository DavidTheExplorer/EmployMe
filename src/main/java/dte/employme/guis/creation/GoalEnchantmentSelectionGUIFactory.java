package dte.employme.guis.creation;

import static dte.employme.conversations.Conversations.createRewardRefundListener;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.EnchantmentLevelPrompt;
import dte.employme.guis.creation.GoalCustomizationGUIFactory.GoalCustomizationGUI;
import dte.employme.job.creation.JobCreationContext;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;
import dte.employme.utils.inventoryframework.RespectingChestGui;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class GoalEnchantmentSelectionGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;
	
	private static final Comparator<Enchantment> ORDER_BY_NAME = comparing(enchantment -> enchantment.getKey().getKey());
	
	public GoalEnchantmentSelectionGUIFactory(GuiConfig config, MessageService messageService)
	{
		this.config = config;
		this.messageService = messageService;
	}
	
	public RespectingChestGui create(Player viewer, JobCreationContext context, Set<Enchantment> enchantmentsToDisplay, GoalCustomizationGUI goalCustomizationGUI)
	{
		RespectingChestGui gui = new RespectingChestGui(new ChestGui(6, this.config.getTitle()));
		
		//add panes
		PaginatedPane enchantmentsPane = getEnchantmentsPane(gui, goalCustomizationGUI, enchantmentsToDisplay, context.getReward());
		
		gui.addPane(createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		gui.addPane(enchantmentsPane);
		gui.addPane(getPanelPane(gui, enchantmentsPane));
		
		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		
		return gui;
	}
	
	private PaginatedPane getEnchantmentsPane(RespectingChestGui enchantmentSelectionGUI, GoalCustomizationGUI goalCustomizationGUI, Set<Enchantment> enchantmentsToDisplay, Reward reward) 
	{
		PaginatedPane pages = new PaginatedPane(0, 0, 9, 5, Priority.LOW);
		pages.addPane(0, new OutlinePane(0, 0, 9, 5, Priority.NORMAL));

		enchantmentsToDisplay.stream()
		.sorted(ORDER_BY_NAME)
		.map(enchantment -> createEnchantedBook(enchantmentSelectionGUI, goalCustomizationGUI, enchantment, reward))
		.forEach(book -> InventoryFrameworkUtils.addItem(book, pages, enchantmentSelectionGUI));
		
		return pages;
	}

	private Pane getPanelPane(RespectingChestGui gui, PaginatedPane enchantmentsPane) 
	{
		OutlinePane pane = new OutlinePane(2, 5, 9, 1);
		pane.setGap(3);
		
		this.config.parseGuiItem("back")
		.whenClicked(backButtonListener(gui, enchantmentsPane))
		.addTo(pane);
		
		this.config.parseGuiItem("next")
		.whenClicked(nextButtonListener(gui, enchantmentsPane))
		.addTo(pane);

		return pane;
	}

	private GuiItem createEnchantedBook(RespectingChestGui enchantmentSelectionGUI, GoalCustomizationGUI goalCustomizationGUI, Enchantment enchantment, Reward reward) 
	{
		ItemStack icon = this.config.parseGuiItem("enchantment-book").build().getItem();
		
		ItemStack enchantmentIcon = new ItemBuilder(icon)
				.named(icon.getItemMeta().getDisplayName().replace("%enchantment%", EnchantmentUtils.getDisplayName(enchantment)))
				.createCopy();
		
		return new GuiItemBuilder()
				.forItem(enchantmentIcon)
				.whenClicked(event -> 
				{
					enchantmentSelectionGUI.removeParent();

					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					beginLevelConversation(player, goalCustomizationGUI, enchantment, reward);
				})
				.build();
	}

	private void beginLevelConversation(Player viewer, GoalCustomizationGUI goalCustomizationGUI, Enchantment enchantment, Reward reward) 
	{
		Conversations.createFactory(this.messageService)
		.withFirstPrompt(new EnchantmentLevelPrompt(enchantment, this.messageService))
		.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
		.addConversationAbandonedListener(createRewardRefundListener(this.messageService, JOB_SUCCESSFULLY_CANCELLED))
		.addConversationAbandonedListener(abandonedEvent -> 
		{
			if(!abandonedEvent.gracefulExit())
				return;

			int level = (int) abandonedEvent.getContext().getSessionData("level");

			goalCustomizationGUI.addEnchantment(enchantment, level);
			goalCustomizationGUI.refundRewardOnClose(true);
			goalCustomizationGUI.show(viewer);
		})
		.buildConversation(viewer)
		.begin();
	}
}