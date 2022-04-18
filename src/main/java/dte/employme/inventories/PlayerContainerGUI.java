package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_BACK;
import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_NEXT_PAGE;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.MasonryPane;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.services.message.MessageService;
import dte.employme.utils.items.ItemBuilder;

public class PlayerContainerGUI extends ChestGui
{
	private final MessageService messageService;

	private final PaginatedPane itemsPane;

	private static final int ITEMS_PER_PAGE = 9 * 4;

	public PlayerContainerGUI(String title, MessageService messageService) 
	{
		super(6, title);

		this.messageService = messageService;

		this.itemsPane = new PaginatedPane(0, 0, 9, ITEMS_PER_PAGE / 9);
		this.itemsPane.addPane(0, createPage());
		
		setOnTopClick(event -> event.setCancelled(true));
		setAbuseListeners();
		addPane(createPanelBackground());		
		addPane(createControlPanel(this.itemsPane));
		addPane(this.itemsPane);
		update();
	}

	public void addItem(ItemStack item) 
	{
		OutlinePane lastPage = (OutlinePane) this.itemsPane.getPanes(this.itemsPane.getPages()-1).iterator().next();
		
		//if the last page is full
		if(lastPage.getItems().size() == this.itemsPane.getHeight()) 
		{
			lastPage = createPage();
			this.itemsPane.addPane(this.itemsPane.getPages(), lastPage);
		}

		lastPage.addItem(createStoredItem(item, lastPage));
	}

	public List<ItemStack> getStoredItems()
	{
		return this.itemsPane.getItems().stream()
				.map(GuiItem::getItem)
				.collect(toList());
	}

	private Pane createPanelBackground() 
	{
		MasonryPane background = new MasonryPane(0, 4, 9, 2, Priority.LOWEST);
		background.addPane(createRectangle(Priority.LOWEST, 0, 4, 9, 1, new GuiItem(createWall(Material.GRAY_STAINED_GLASS_PANE))));
		background.addPane(createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));


		return background;
	}

	private Pane createControlPanel(PaginatedPane itemsPane) 
	{
		OutlinePane panel = new OutlinePane(2, 5, 9, 1, Priority.LOW);
		panel.setGap(3);

		panel.addItem(createBackButton("MHF_ArrowLeft", itemsPane));
		panel.addItem(createNextButton("MHF_ArrowRight", itemsPane));

		return panel;
	}

	private OutlinePane createPage() 
	{
		return new OutlinePane(0, 0, 9, this.itemsPane.getHeight());
	}

	@SuppressWarnings("deprecation")
	private GuiItem createNextButton(String ownerName, PaginatedPane itemsPane)
	{
		ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
				.withItemMeta(SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerName)))
				.named(this.messageService.getMessage(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE).first())
				.createCopy();

		return new GuiItem(item, event -> 
		{
			if(itemsPane.getPage() == itemsPane.getPages()-1)
				return;

			itemsPane.setPage(itemsPane.getPage()+1);
			update();
		});
	}

	@SuppressWarnings("deprecation")
	private GuiItem createBackButton(String ownerName, PaginatedPane itemsPane)
	{
		ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
				.withItemMeta(SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerName)))
				.named(this.messageService.getMessage(INVENTORY_PLAYER_CONTAINER_BACK).first())
				.createCopy();

		return new GuiItem(item, event -> 
		{
			if(itemsPane.getPage() == 0) 
				return;

			itemsPane.setPage(itemsPane.getPage()-1);
			update();
		});
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
	
	@SuppressWarnings("incomplete-switch")
	private void setAbuseListeners() 
	{
		setOnBottomClick(event ->
		{
			if(event.isShiftClick())
				event.setCancelled(true);
		});

		setOnTopClick(event ->
		{
			event.setCancelled(true);

			InventoryView view = event.getView();

			if(event.isShiftClick() && event.getClickedInventory() == view.getBottomInventory()) 
				event.setCancelled(true);

			if(event.getClickedInventory() == view.getTopInventory() && event.getCursor() != null)
			{
				switch(event.getAction())
				{
				case PLACE_ALL:
				case PLACE_ONE:
				case PLACE_SOME:
				case HOTBAR_SWAP:
					event.setCancelled(true);
				}
			}
		});

		setOnTopDrag(event -> 
		{
			if(event.getOldCursor() == null)
				return;

			event.setCancelled(true);
		});
	}
}