package dte.employme.utils.inventoryframework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryCloseEvent;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import dte.employme.utils.forwarding.ForwardingChestGui;

public class ListenableChestGui extends ForwardingChestGui
{
	private final List<Consumer<InventoryCloseEvent>> closeListeners = new ArrayList<>();
	
	public ListenableChestGui(ChestGui gui)
	{
		super(gui);
	}
	
	public void addCloseListener(Consumer<InventoryCloseEvent> closeListener) 
	{
		this.closeListeners.add(closeListener);
		
		refreshCloseListeners();
	}
	
	public void removeCloseListener(Consumer<InventoryCloseEvent> closeListener) 
	{
		this.closeListeners.remove(closeListener);
		
		refreshCloseListeners();
	}
	
	private void refreshCloseListeners()
	{
		setOnClose(event -> this.closeListeners.forEach(listener -> listener.accept(event)));
	}
}
