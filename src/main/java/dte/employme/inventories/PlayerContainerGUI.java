package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_BACK;
import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_NEXT_PAGE;
import static dte.employme.utils.InventoryFrameworkUtils.BACK_BUTTON;
import static dte.employme.utils.InventoryFrameworkUtils.NEXT_BUTTON;
import static dte.employme.utils.InventoryFrameworkUtils.createBackButtonListener;
import static dte.employme.utils.InventoryFrameworkUtils.createNextButtonListener;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.services.message.MessageService;
import dte.employme.utils.GuiItemBuilder;

public class PlayerContainerGUI extends ChestGui
{
	private final MessageService messageService;
	private final PaginatedPane itemsPane;

	private static final int ITEMS_PER_PAGE = 9 * 5;

	public PlayerContainerGUI(String title, MessageService messageService) 
	{
		super(6, title);

		this.messageService = messageService;

		this.itemsPane = createItemsPane();
		addPage();

		setOnTopClick(event -> event.setCancelled(true));
		setAbuseListeners();	
		addPane(createControlPanel(this.itemsPane));
		addPane(createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(this.itemsPane);
		update();
	}

	public void addItem(ItemStack item) 
	{
		OutlinePane lastPage = (OutlinePane) this.itemsPane.getPanes(this.itemsPane.getPages()-1).iterator().next();

		if(lastPage.getItems().size() == ITEMS_PER_PAGE) 
			lastPage = addPage();

		lastPage.addItem(createStoredItem(item, lastPage));
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
				.forItem(BACK_BUTTON.named(this.messageService.getMessage(INVENTORY_PLAYER_CONTAINER_BACK).first()).createCopy())
				.whenClicked(createBackButtonListener(this, this.itemsPane))
				.build());

		panel.addItem(new GuiItemBuilder()
				.forItem(NEXT_BUTTON.named(this.messageService.getMessage(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE).first()).createCopy())
				.whenClicked(createNextButtonListener(this, this.itemsPane))
				.build());

		return panel;
	}

	private PaginatedPane createItemsPane() 
	{
		return new PaginatedPane(0, 0, 9, ITEMS_PER_PAGE / 9);
	}

	private OutlinePane addPage() 
	{
		OutlinePane page = new OutlinePane(0, 0, 9, this.itemsPane.getHeight());
		this.itemsPane.addPane(this.itemsPane.getPages(), page);

		return page;
	}

	private GuiItem createStoredItem(ItemStack item, OutlinePane page) 
	{
		GuiItem guiItem = new GuiItem(item);
		
		guiItem.setAction(event -> 
		{
			boolean givenToPlayer = !event.getWhoClicked().getInventory().addItem(item).isEmpty();
			
			//do nothing if the player can't get the item
			if(givenToPlayer)
				return;

			//remove the clicked item
			page.removeItem(guiItem);

			//delete the page if it's now empty, unless it's the only page left
			if(page.getItems().isEmpty() && this.itemsPane.getPages() > 1)
			{
				int previousPage = this.itemsPane.getPage() == 0 ? 0 : this.itemsPane.getPage()-1;

				this.itemsPane.deletePage(this.itemsPane.getPage());
				this.itemsPane.setPage(previousPage);
			}
			
			update();
		});

		return guiItem;
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