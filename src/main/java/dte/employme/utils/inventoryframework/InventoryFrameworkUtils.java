package dte.employme.utils.inventoryframework;

import static dte.employme.utils.InventoryUtils.createWall;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;

import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.StringUtils;

public class InventoryFrameworkUtils
{
	/**
	 * Creates a Pane in a rectangular shape, and completely fills it with the provided {@code item}. 
	 * The shape starts in the provided {@code x, y} and has the provided {@code length, height}.
	 * 
	 * @param priority The priority of the pane.
	 * @param x The start X of the pane.
	 * @param y The start Y of the pane.
	 * @param length The length of the pane.
	 * @param height The height of the pane.
	 * @param item The item to fill the pane with.
	 * @return A rectangle-like pane.
	 */
	public static Pane createRectangle(Priority priority, int x, int y, int length, int height, GuiItem item) 
	{
		OutlinePane pane = new OutlinePane(x, y, length, height, priority);
		pane.addItem(item);
		pane.setRepeat(true);

		return pane;
	}

	/**
	 * Creates a Pane in a square shape, and completely fills it with the provided {@code item}.
	 * The shape starts in the provided {@code x, y} and its vertical/horizontal heights are the provided {@code length}.
	 * 
	 * @param priority The priority of the pane.
	 * @param x The start X of the pane.
	 * @param y The start Y of the pane.
	 * @param length The vertical/horizontal heights of the pane.
	 * @param item The item to fill the pane with.
	 * @return A square-like pane.
	 */
	public static Pane createSquare(Priority priority, int x, int y, int length, GuiItem item) 
	{
		return createRectangle(priority, x, y, length, length, item);
	}

	/**
	 * Creates a Pane that represents a border that surrounds the entire provided {@code pane}, and fills it with the provided {@code item}.
	 * 
	 * @param gui The GUI to create walls for.
	 * @param priority The priority of the pane.
	 * @param item The item to fill the pane with.
	 * @return A border pane for the provided GUI.
	 */
	public static Pane createWalls(ChestGui gui, Priority priority) 
	{
		PatternPane background = new PatternPane(0, 0, 9, gui.getRows(), createWallsPattern(gui));
		background.bindItem('W', new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE)));
		background.setPriority(priority);

		return background;
	}
	
	
	
	/**
     * Creates a pane for a single item, at the provided {@code x, y} position.
     * 
     * @param x The pane's X position.
     * @param y The pane's Y position.
     * @param priority The pane's priority.
     * @param item The item to put in the pane.
     * @return A pane for the provided item.
     */
	public static StaticPane createItemPane(int x, int y, GuiItem item) 
	{
		return createItemPane(x, y, Priority.NORMAL, item);
	}
	
	/**
	 * Works like {@link InventoryFrameworkUtils#createItemPane(int, int, GuiItem)} but the priority is passed instead of defaulting to {@code NORMAL}.
	 * 
	 * @see InventoryFrameworkUtils#createItemPane(int, int, GuiItem)
	 */
	public static StaticPane createItemPane(int x, int y, Priority priority, GuiItem item) 
	{
		StaticPane pane = new StaticPane(x, y, 1, 1, priority);
		pane.addItem(item, 0, 0);

		return pane;
	}
	
	
	
	public static Consumer<InventoryClickEvent> backButtonListener(Gui gui, PaginatedPane pages)
	{
		return event -> 
		{
			if(pages.getPage() == 0)
				return;

			pages.setPage(pages.getPage()-1);
			gui.update();
		};
	}

	public static Consumer<InventoryClickEvent> nextButtonListener(Gui gui, PaginatedPane pages)
	{
		return event -> 
		{
			if(pages.getPage() == pages.getPages()-1)
				return;

			pages.setPage(pages.getPage()+1);
			gui.update();
		};
	}
	
	
	
	public static Pattern createWallsPattern(ChestGui gui) 
	{
		List<String> pattern = new ArrayList<>();
		pattern.add(StringUtils.repeat("W", 9));

		for(int i = 1, rows = gui.getRows()-2; i <= rows; i++)
			pattern.add("W" + StringUtils.repeat(" ", 7) + "W");

		pattern.add(StringUtils.repeat("W", 9));

		return new Pattern(pattern.toArray(new String[0]));
	}
	
	
	/**
	 * Creates a page for the provided {@code PaginatedPage} which starts at {@code 0, 0} and ends in its bottom right corner.
	 * 
	 * @param pages The PaginatedPage to create a page for.
	 * @return The page.
	 */
	public static OutlinePane createPage(PaginatedPane pages) 
	{
		return new OutlinePane(0, 0, pages.getLength(), pages.getHeight());
	}
	
	/**
	 * Works like {@link #addItem(Function, PaginatedPane, Gui)} but an {@code item} is passed about the last page object.
	 * 
	 * @see #addItem(Function, PaginatedPane, Gui)
	 */
	public static void addItem(GuiItem item, PaginatedPane pages, Gui gui) 
	{
		addItem(lastPage -> item, pages, gui);
	}
	
	/**
	 * Adds an item(calculated using the last page) to the last page in {@code pages}, automatically creating a new page(using {@link #createPage(PaginatedPane)} if the last page is full.
	 * 
	 * @param itemByLastPage The function that determins the item to add, using the last page - which can be the current one, or a new one if the current is full.
	 * @param pages The PaginatedPane.
	 * @param gui The GUI where the PaginatedPane is inside.
	 */
	public static void addItem(Function<OutlinePane, GuiItem> itemByLastPage, PaginatedPane pages, Gui gui) 
	{
		OutlinePane lastPage = (OutlinePane) pages.getPanes(pages.getPages()-1).iterator().next();

		//if the last page is full, create another page
		if(lastPage.getItems().size() == (pages.getHeight() * pages.getLength()))
		{
			lastPage = createPage(pages);
			pages.addPane(pages.getPages(), lastPage);
		}

		lastPage.addItem(itemByLastPage.apply(lastPage));
		gui.update();
	}
	
	/**
	 * Removes the provided {@code item} from the provided {@code page}, automatically removing the page if consequently it became empty AND <b>is not the first page</b>.
	 * 
	 * @param pages The PaginatedPane.
	 * @param page The page to remove the item from.
	 * @param item The item to remove.
	 */
	public static void removeItem(PaginatedPane pages, OutlinePane page, GuiItem item) 
	{
		//remove the clicked item
		page.removeItem(item);

		//don't do anything if more items are left or it's the first page
		if(!page.getItems().isEmpty() || pages.getPages() == 1)
			return;

		int previousPage = pages.getPage() == 0 ? 0 : pages.getPage()-1;
		pages.deletePage(pages.getPage());
		pages.setPage(previousPage);
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static ItemBuilder backButtonBuilder() 
	{
		return new ItemBuilder(Material.PLAYER_HEAD)
				.withItemMeta(SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft")));
	}

	@SuppressWarnings("deprecation")
	public static ItemBuilder nextButtonBuilder() 
	{
		return new ItemBuilder(Material.PLAYER_HEAD)
				.withItemMeta(SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight")));
	}
}