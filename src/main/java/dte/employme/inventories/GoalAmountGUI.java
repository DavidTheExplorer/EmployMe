package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_GOAL_AMOUNT_FINISH_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_AMOUNT_FINISH_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_AMOUNT_NUMERIC_AMOUNT_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createItemPane;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.BLACK;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;

import dte.employme.messages.service.MessageService;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.NumberUtils;

public class GoalAmountGUI extends AnvilGui
{
	private final GoalCustomizationGUI goalCustomizationGUI;
	private final MessageService messageService;

	public GoalAmountGUI(GoalCustomizationGUI goalCustomizationGUI, MessageService messageService)
	{
		super("Specify the Amount:");

		this.goalCustomizationGUI = goalCustomizationGUI;
		this.messageService = messageService;

		setOnGlobalClick(event -> event.setCancelled(true));
		getFirstItemComponent().addPane(createItemPane(0, 0, createGoalItem()));
		getSecondItemComponent().addPane(createItemPane(0, 0, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		getResultComponent().addPane(createItemPane(0, 0, createFinishItem()));
		update();
	}
	
	private GuiItem createGoalItem() 
	{
		return new GuiItem(new ItemBuilder(this.goalCustomizationGUI.getType())
				.named(BLACK.toString() + this.goalCustomizationGUI.getAmount())
				.createCopy());
	}

	/*
	 * Items
	 */

	private GuiItem createFinishItem() 
	{
		return new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA)
				.named(this.messageService.getMessage(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_NAME).first())
				.withLore(this.messageService.getMessage(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_LORE).toArray())
				.createCopy(), 
				event -> 
		{
			Integer enteredAmount = NumberUtils.parseInt(getRenameText())
					.filter(amount -> amount > 0)
					.orElse(null);

			if(enteredAmount == null)
			{
				setTitle(this.messageService.getMessage(INVENTORY_GOAL_AMOUNT_NUMERIC_AMOUNT_TITLE).first());
				update();
				return;
			}
			
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();

			this.goalCustomizationGUI.setAmount(enteredAmount);
			this.goalCustomizationGUI.setRefundRewardOnClose(true);
			this.goalCustomizationGUI.show(player);
		});
	}
}