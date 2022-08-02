package dte.employme.utils;

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
	/*
	 * Shapes
	 */
	public static Pane createRectangle(Priority priority, int x, int y, int length, int height, GuiItem item) 
	{
		OutlinePane pane = new OutlinePane(x, y, length, height, priority);
		pane.addItem(item);
		pane.setRepeat(true);

		return pane;
	}

	public static Pane createSquare(Priority priority, int x, int y, int length, GuiItem item) 
	{
		return createRectangle(priority, x, y, length, length, item);
	}

	public static Pane createWalls(ChestGui gui, Priority priority) 
	{
		PatternPane background = new PatternPane(0, 0, 9, gui.getRows(), createWallsPattern(gui));
		background.bindItem('W', new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE)));
		background.setPriority(priority);

		return background;
	}



	/*
	 * Single Item Pane
	 */
	public static StaticPane createItemPane(int x, int y, GuiItem item) 
	{
		return createItemPane(x, y, Priority.NORMAL, item);
	}

	public static StaticPane createItemPane(int x, int y, Priority priority, GuiItem item) 
	{
		StaticPane pane = new StaticPane(x, y, 1, 1, priority);
		pane.addItem(item, 0, 0);

		return pane;
	}



	/*
	 * InventoryClickEvent Handlers
	 */
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



	/*
	 * Patterns
	 */
	public static Pattern createWallsPattern(ChestGui gui) 
	{
		List<String> pattern = new ArrayList<>();
		pattern.add(StringUtils.repeat("W", 9));

		for(int i = 1, rows = gui.getRows()-2; i <= rows; i++)
			pattern.add("W" + StringUtils.repeat(" ", 7) + "W");

		pattern.add(StringUtils.repeat("W", 9));

		return new Pattern(pattern.toArray(new String[0]));
	}



	/*
	 * PaginatedPane
	 */
	public static OutlinePane createPage(PaginatedPane pages) 
	{
		return new OutlinePane(0, 0, pages.getLength(), pages.getHeight());
	}

	public static void addItem(GuiItem item, PaginatedPane pages, Gui gui) 
	{
		addItem(lastPage -> item, pages, gui);
	}

	public static void addItem(Function<OutlinePane, GuiItem> itemByLastPage, PaginatedPane pages, Gui gui) 
	{
		OutlinePane lastPage = (OutlinePane) pages.getPanes(pages.getPages()-1).iterator().next();

		if(lastPage.getItems().size() == (pages.getHeight() * pages.getLength()))
		{
			lastPage = createPage(pages);
			pages.addPane(pages.getPages(), lastPage);
		}

		lastPage.addItem(itemByLastPage.apply(lastPage));
		gui.update();
	}
	
	
	
	/*
	 * Items
	 */
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