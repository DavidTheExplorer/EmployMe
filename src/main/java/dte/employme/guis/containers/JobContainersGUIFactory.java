package dte.employme.guis.containers;

import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;

public class JobContainersGUIFactory
{
	private final GuiConfig config;
	private final PlayerContainerService playerContainerService;
	
	public JobContainersGUIFactory(GuiConfig config, PlayerContainerService playerContainerService) 
	{
		this.config = config;
		this.playerContainerService = playerContainerService;
	}
	
	public ChestGui create(Player viewer) 
	{
		ChestGui gui = new ChestGui(1, this.config.getTitle());
		
		//add panes
		gui.addPane(parseBackground());
		gui.addPane(parseContainersPane(gui, viewer));
		
		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		
		return gui;
	}
	
	private Pane parseBackground() 
	{
		return createRectangle(Priority.LOWEST, 0, 0, 9, 1, this.config.parseGuiItem("background").build());
	}
	
	private OutlinePane parseContainersPane(ChestGui jobContainersGUI, Player viewer) 
	{
		OutlinePane pane = new OutlinePane(2, 0, 9, 1, Priority.LOW);
		pane.setGap(3);
		pane.addItem(parseItemsContainerItem(jobContainersGUI, viewer));
		pane.addItem(parseRewardsContainerItem(jobContainersGUI, viewer));
		
		return pane;
	}
	
	private GuiItem parseItemsContainerItem(ChestGui jobContainersGUI, Player viewer) 
	{
		return this.config.parseGuiItem("items-container")
				.whenClicked(event -> 
				{
					ItemPaletteGUI container = this.playerContainerService.getItemsContainer(viewer.getUniqueId());
					container.setOnClose(closeEvent -> jobContainersGUI.show(viewer));
					container.show(viewer);
				})
				.build();
	}
	
	private GuiItem parseRewardsContainerItem(ChestGui jobContainersGUI, Player viewer) 
	{
		return this.config.parseGuiItem("rewards-container")
				.whenClicked(event -> 
				{
					ItemPaletteGUI container = this.playerContainerService.getRewardsContainer(viewer.getUniqueId());
					container.setOnClose(closeEvent -> jobContainersGUI.show(viewer));
					container.show(viewer);
				})
				.build();
	}
}
