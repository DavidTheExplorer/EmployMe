package dte.employme.inventories;

import static dte.employme.utils.InventoryFrameworkUtils.createItemPane;
import static dte.employme.utils.InventoryUtils.createWall;
import static net.md_5.bungee.api.ChatColor.RED;
import static org.bukkit.ChatColor.BLACK;
import static org.bukkit.ChatColor.GREEN;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;

import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.NumberUtils;

public class GoalAmountGUI extends AnvilGui
{
	private final GoalCustomizationGUI goalCustomizationGUI;

	public GoalAmountGUI(GoalCustomizationGUI goalCustomizationGUI)
	{
		super("Specify the Amount:");

		this.goalCustomizationGUI = goalCustomizationGUI;

		setOnGlobalClick(event -> event.setCancelled(true));
		getFirstItemComponent().addPane(createItemPane(0, 0, createGoalItem()));
		getSecondItemComponent().addPane(createItemPane(0, 0, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		getResultComponent().addPane(createItemPane(0, 0, createContinueItem()));
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

	private GuiItem createContinueItem() 
	{
		return new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA)
				.named(GREEN + "Continue")
				.createCopy(), 
				event -> 
		{
			Integer enteredAmount = NumberUtils.parseInt(getRenameText())
					.filter(amount -> amount > 0)
					.orElse(null);

			if(enteredAmount == null)
			{
				setTitle(RED + "Enter Numeric Amount:");
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