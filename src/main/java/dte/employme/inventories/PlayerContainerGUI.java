package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_BACK;
import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_CONTAINER_NEXT_PAGE;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Map<OutlinePane, Map<ItemStack, GuiItem>> panesItems = new HashMap<>();

	private static final int ITEMS_PER_PAGE = 9 * 4;

	public PlayerContainerGUI(String title, MessageService messageService) 
	{
		super(6, title);
		
		this.messageService = messageService;

		this.itemsPane = new PaginatedPane(0, 0, 9, ITEMS_PER_PAGE / 9);
		this.itemsPane.addPane(0, createPage());

		setAbuseListeners();
		addPane(createPanelBackground());		
		addPane(createControlPanel(this.itemsPane));
		addPane(this.itemsPane);
		update();
	}

	public void addItem(ItemStack item) 
	{
		GuiItem guiItem = new GuiItem(item);
		OutlinePane lastPage = (OutlinePane) this.itemsPane.getPanes(this.itemsPane.getPages()-1).iterator().next();

		if(lastPage.getItems().size() == ITEMS_PER_PAGE) 
		{
			lastPage = createPage();
			this.itemsPane.addPane(this.itemsPane.getPages(), lastPage);
		}

		lastPage.addItem(guiItem);
		this.panesItems.get(lastPage).put(item, guiItem);
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
		background.setOnClick(event -> event.setCancelled(true));

		return background;
	}

	private Pane createControlPanel(PaginatedPane itemsPane) 
	{
		OutlinePane panel = new OutlinePane(2, 5, 9, 1, Priority.LOW);
		panel.setOnClick(event -> event.setCancelled(true));
		panel.setOrientation(HORIZONTAL);
		panel.setGap(3);
		
		panel.addItem(createBackButton("MHF_ArrowLeft", itemsPane));
		panel.addItem(createNextButton("MHF_ArrowRight", itemsPane));
		
		return panel;
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

	private OutlinePane createPage() 
	{
		OutlinePane page = new OutlinePane(0, 0, 9, ITEMS_PER_PAGE / 9);
		page.setOrientation(HORIZONTAL);

		page.setOnClick(event -> 
		{
			ItemStack item = event.getCurrentItem();

			if(item == null)
				return;
			
			if(!event.getWhoClicked().getInventory().addItem(item).isEmpty())
				return;
			
			//remove the clicked item
			Map<ItemStack, GuiItem> pageItems = this.panesItems.get(page);
			page.removeItem(pageItems.get(item));
			pageItems.remove(item);
			
			if(pageItems.isEmpty() && this.itemsPane.getPages() > 1)
			{
				int previousPage = this.itemsPane.getPage() == 0 ? 0 : this.itemsPane.getPage()-1;

				this.itemsPane.deletePage(this.itemsPane.getPage());
				this.itemsPane.setPage(previousPage);
			}

			update();
		});

		this.panesItems.put(page, new HashMap<>());

		return page;
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
}