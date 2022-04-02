package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.Comparator.comparing;
import static org.bukkit.ChatColor.GREEN;

import java.util.Comparator;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.EnchantmentLevelPrompt;
import dte.employme.job.rewards.Reward;
import dte.employme.services.job.reward.JobRewardService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.EnchantmentUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class GoalEnchantmentSelectionGUI extends ChestGui
{
	private final MessageService messageService;
	private final GoalCustomizationGUI goalCustomizationGUI;
	private final Reward reward;
	private final JobRewardService jobRewardService;
	
	private boolean showCustomizationGUIOnClose = true;

	private static final Comparator<Enchantment> ORDER_BY_NAME = comparing(enchantment -> enchantment.getKey().getKey());

	public GoalEnchantmentSelectionGUI(MessageService messageService, GoalCustomizationGUI goalCustomizationGUI, Reward reward, JobRewardService jobRewardService)
	{
		super(6, messageService.getMessage(INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE).first());
		
		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;
		this.reward = reward;
		this.jobRewardService = jobRewardService;

		setOnClose(event -> 
		{
			if(!this.showCustomizationGUIOnClose)
				return;
			
			goalCustomizationGUI.setRefundRewardOnClose(true);
			goalCustomizationGUI.show(event.getPlayer());
		});

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(getEnchantmentsPane());
		update();
	}

	private Pane getEnchantmentsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, Priority.LOW);

		EnchantmentUtils.getRemainingEnchantments(this.goalCustomizationGUI.getCurrentItem()).stream()
		.sorted(ORDER_BY_NAME)
		.map(this::createEnchantedBook)
		.forEach(pane::addItem);

		return pane;
	}

	private GuiItem createEnchantedBook(Enchantment enchantment) 
	{
		ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK)
				.named(GREEN + EnchantmentUtils.getDisplayName(enchantment))
				.withLore(this.messageService.getMessage(INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE).toArray())
				.createCopy();

		return new GuiItem(item, event -> 
		{
			this.showCustomizationGUIOnClose = false;
			
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();
			
			Conversations.createFactory(this.messageService)
			.withFirstPrompt(new EnchantmentLevelPrompt(enchantment, this.messageService))
			.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", this.reward).build())
			.addConversationAbandonedListener(Conversations.refundRewardIfAbandoned(this.jobRewardService))
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
		});
	}
}