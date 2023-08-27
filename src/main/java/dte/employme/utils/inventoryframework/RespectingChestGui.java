package dte.employme.utils.inventoryframework;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.event.inventory.InventoryCloseEvent;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;

public class RespectingChestGui extends ListenableChestGui
{
	private Consumer<InventoryCloseEvent> respectListener;
	
	public RespectingChestGui(ChestGui gui) 
	{
		super(gui);
	}
	
	public RespectingChestGui(ChestGui gui, Gui parent)
	{
		this(gui);
		
		openOnClose(parent);
	}
	
	public void openOnClose(Gui parent) 
	{
		removeParent();
		openOnClose(() -> parent);
	}
	
	public void openOnClose(Supplier<Gui> parentSupplier) 
	{
		Objects.requireNonNull(parentSupplier, "The parent GUI to open on close must be provided!");
		
		addCloseListener(this.respectListener = createRespectListener(parentSupplier));
	}
	
	public void removeParent() 
	{
		removeCloseListener(this.respectListener);
		
		this.respectListener = null;
	}
	
	public boolean hasParent() 
	{
		return this.respectListener != null;
	}
	
	private Consumer<InventoryCloseEvent> createRespectListener(Supplier<Gui> parentSupplier)
	{
		return (event) -> parentSupplier.get().show(event.getPlayer());
	}
}
