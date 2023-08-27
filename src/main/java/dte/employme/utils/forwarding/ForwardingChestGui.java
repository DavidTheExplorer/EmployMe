package dte.employme.utils.forwarding;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.stefvanschie.inventoryframework.HumanEntityCache;
import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.Pane;

public abstract class ForwardingChestGui extends ChestGui
{
	private final ChestGui gui;
	
	protected ForwardingChestGui(ChestGui gui)
	{
		super(gui.getRows(), gui.getTitleHolder(), getPlugin(gui));
		
		this.gui = gui;
	}
	
	@Override
	public @NotNull InventoryComponent getInventoryComponent() 
	{
		return this.gui.getInventoryComponent();
	}
	
	@Override
	public int getRows() 
	{
		return this.gui.getRows();
	}
	
	@Override
	public @NotNull Inventory createInventory() 
	{
		return this.gui.createInventory();
	}
	
	@Override
	public @NotNull Collection<GuiItem> getItems() 
	{
		return this.gui.getItems();
	}
	
	@Override
	public @NotNull List<Pane> getPanes() 
	{
		return this.gui.getPanes();
	}
	
	@Override
	public void addPane(@NotNull Pane pane) 
	{
		this.gui.addPane(pane);
	}
	
	@Override
	public @NotNull Inventory getInventory() 
	{
		return this.gui.getInventory();
	}
	
	@Override
	public void setRows(int rows) 
	{
		this.gui.setRows(rows);
	}
	
	@Override
	public void setTitle(@NotNull String title) 
	{
		this.gui.setTitle(title);
	}
	
	@Override
	public @NotNull String getTitle() 
	{
		return this.gui.getTitle();
	}
	
	@Override
	public @NotNull TextHolder getTitleHolder() 
	{
		return this.gui.getTitleHolder();
	}
	
	@Override
	public void setTitle(@NotNull TextHolder title) 
	{
		this.gui.setTitle(title);
	}
	
	@Override
	public boolean isDirty() 
	{
		return this.gui.isDirty();
	}
	
	@Override
	public void markChanges() 
	{
		this.gui.markChanges();
	}

	@Override
	public void show(@NotNull HumanEntity humanEntity) 
	{
		this.gui.show(humanEntity);
	}

	@Override
	public @NotNull ChestGui copy() 
	{
		return this.gui.copy();
	}

	@Override
	public void click(@NotNull InventoryClickEvent event) 
	{
		this.gui.click(event);
	}
	
	@Override
	public boolean isPlayerInventoryUsed() 
	{
		return this.gui.isPlayerInventoryUsed();
	}

	@Override
	public int getViewerCount() 
	{
		return this.gui.getViewerCount();
	}

	@Override
	public @NotNull List<HumanEntity> getViewers() 
	{
		return this.gui.getViewers();
	}
	
	@Override
	public void update() 
	{
		this.gui.update();
	}
	
	@Override
	public @NotNull HumanEntityCache getHumanEntityCache() 
	{
		return this.gui.getHumanEntityCache();
	}
	
	@Override
	public void setOnTopClick(@Nullable Consumer<InventoryClickEvent> onTopClick) 
	{
		this.gui.setOnTopClick(onTopClick);
	}
	
	@Override
	public void callOnTopClick(@NotNull InventoryClickEvent event) 
	{
		this.gui.callOnTopClick(event);
	}
	
	@Override
	public void setOnBottomClick(@Nullable Consumer<InventoryClickEvent> onBottomClick) 
	{
		this.gui.setOnBottomClick(onBottomClick);
	}
	
	@Override
	public void callOnBottomClick(@NotNull InventoryClickEvent event) 
	{
		this.gui.callOnBottomClick(event);
	}
	
	@Override
	public void setOnGlobalClick(@Nullable Consumer<InventoryClickEvent> onGlobalClick) 
	{
		this.gui.setOnGlobalClick(onGlobalClick);
	}
	
	@Override
	public void callOnGlobalClick(@NotNull InventoryClickEvent event) 
	{
		this.gui.callOnGlobalClick(event);
	}
	
	@Override
	public void setOnOutsideClick(@Nullable Consumer<InventoryClickEvent> onOutsideClick) 
	{
		this.gui.setOnOutsideClick(onOutsideClick);
	}
	
	@Override
	public void callOnOutsideClick(@NotNull InventoryClickEvent event) 
	{
		this.gui.callOnOutsideClick(event);
	}
	
	@Override
	public void setOnTopDrag(@Nullable Consumer<InventoryDragEvent> onTopDrag) 
	{
		this.gui.setOnTopDrag(onTopDrag);
	}
	
	@Override
	public void callOnTopDrag(@NotNull InventoryDragEvent event) 
	{
		this.gui.callOnTopDrag(event);
	}
	
	@Override
	public void setOnBottomDrag(@Nullable Consumer<InventoryDragEvent> onBottomDrag) 
	{
		this.gui.setOnBottomDrag(onBottomDrag);
	}
	
	@Override
	public void callOnBottomDrag(@NotNull InventoryDragEvent event)
	{
		this.gui.callOnBottomDrag(event);
	}
	
	@Override
	public void setOnGlobalDrag(@Nullable Consumer<InventoryDragEvent> onGlobalDrag) 
	{
		this.gui.setOnGlobalDrag(onGlobalDrag);
	}
	
	@Override
	public void callOnGlobalDrag(@NotNull InventoryDragEvent event) 
	{
		this.gui.callOnBottomDrag(event);
	}
	
	@Override
	public void setOnClose(@Nullable Consumer<InventoryCloseEvent> onClose) 
	{
		this.gui.setOnClose(onClose);
	}
	
	@Override
	public void callOnClose(@NotNull InventoryCloseEvent event)
	{
		this.gui.callOnClose(event);
	}
	
	@Override
	public boolean isUpdating() 
	{
		return this.gui.isUpdating();
	}
	
	private static Plugin getPlugin(ChestGui gui)
	{
		try 
		{
			Field field = Gui.class.getDeclaredField("plugin");
			field.setAccessible(true);
			
			return (Plugin) field.get(gui);
		} 
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
}