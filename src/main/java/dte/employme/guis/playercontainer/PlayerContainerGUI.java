package dte.employme.guis.playercontainer;

import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.toMinecraftItem;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.services.message.MessageService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;

public class PlayerContainerGUI extends ChestGui
{
	private final MessageService messageService;
	private final PaginatedPane itemsPane;

	private static final int ITEMS_PER_PAGE = 9 * 5;

	public PlayerContainerGUI(String title, MessageService messageService) 
	{
		super(6, title);

		this.messageService = messageService;

		this.itemsPane = new PaginatedPane(0, 0, 9, ITEMS_PER_PAGE / 9, Priority.LOWEST);
		this.itemsPane.addPane(0, createPage(this.itemsPane));

		setAbuseListeners();	
		addPane(createControlPanel(this.itemsPane));
		addPane(createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(this.itemsPane);
	}

	public void addItem(ItemStack item)
	{
		/*
		 * InventoryFramework can't handle big items, meaning ItemStack(Material.SNOWBALL, 40) would be shown with the amount of 16.
		 * ItemStackUtils#divideBigItem offers a solution: it splits the item into multiple, each one with the max stack amount.
		 */
		for(ItemStack smallerItem : ItemStackUtils.divide(item))
			InventoryFrameworkUtils.addItem(lastPage -> createStoredItem(smallerItem, lastPage), this.itemsPane, this);
	}

	public List<ItemStack> getStoredItems()
	{
		return this.itemsPane.getItems().stream()
				.map(GuiItem::getItem)
				.collect(toList());
	}

	private Pane createControlPanel(PaginatedPane itemsPane) 
	{
		OutlinePane panel = new OutlinePane(2, 5, 9, 1, Priority.LOW);
		panel.setGap(3);

		panel.addItem(new GuiItemBuilder()
				.forItem(backButtonBuilder()
						.named(this.messageService.loadMessage(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(backButtonListener(this, this.itemsPane))
				.build());

		panel.addItem(new GuiItemBuilder()
				.forItem(nextButtonBuilder()
						.named(this.messageService.loadMessage(GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(nextButtonListener(this, this.itemsPane))
				.build());

		return panel;
	}

	private GuiItem createStoredItem(ItemStack item, OutlinePane page) 
	{
		return new GuiItemBuilder().forItem(item)
				.whenClicked((event, guiItem) -> 
				{
					Map<Integer, ItemStack> left = event.getWhoClicked().getInventory().addItem(toMinecraftItem(guiItem));

					//remove the item if it fitted in the player's inventory
					if(left.isEmpty()) 
						InventoryFrameworkUtils.removeItem(this.itemsPane, page, guiItem);
					else
						guiItem.getItem().setAmount(left.get(0).getAmount());
					
					update();
				})
				.build();
	}

	private void setAbuseListeners() 
	{
		setOnTopClick(event -> event.setCancelled(true));

		//shifting items into the inventory
		setOnBottomClick(event ->
		{
			if(event.isShiftClick())
				event.setCancelled(true);
		});

		//dragging items into the inventory
		setOnTopDrag(event -> 
		{
			if(event.getOldCursor() == null)
				return;

			event.setCancelled(true);
		});
	}
}