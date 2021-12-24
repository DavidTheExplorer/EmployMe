package dte.employme.utils;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

public class InventoryFrameworkUtils
{
	//Container of static methods
	private InventoryFrameworkUtils(){}
	
	public static OutlinePane createRectangle(Priority priority, int x, int y, int length, int height, GuiItem item) 
	{
		OutlinePane pane = new OutlinePane(x, y, length, height, priority);
		pane.addItem(item);
		pane.setRepeat(true);
		return pane;
	}
	
	public static OutlinePane createSquare(Priority priority, int x, int y, int length, GuiItem item) 
	{
		return createRectangle(priority, x, y, length, length, item);
	}
}