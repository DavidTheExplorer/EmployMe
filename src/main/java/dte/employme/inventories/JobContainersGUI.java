package dte.employme.inventories;

import static dte.employme.messages.MessageKey.GUI_JOB_CONTAINERS_ITEMS_CONTAINER_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_CONTAINERS_ITEMS_CONTAINER_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_CONTAINERS_REWARDS_CONTAINER_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_CONTAINERS_REWARDS_CONTAINER_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_CONTAINERS_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.items.ItemBuilder;

public class JobContainersGUI extends ChestGui
{
	private final PlayerContainerService playerContainerService;
	private final MessageService messageService;
	
	public JobContainersGUI(MessageService messageService, PlayerContainerService playerContainerService) 
	{
		super(1, messageService.getMessage(GUI_JOB_CONTAINERS_TITLE).first());
		
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
		
		setOnGlobalClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 1, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createContainersPane());
		update();
	}
	
	private Pane createContainersPane() 
	{
		OutlinePane pane = new OutlinePane(2, 0, 9, 1, Priority.LOW);
		pane.setGap(3);
		
		pane.addItem(createContainerIcon(this.messageService.getMessage(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_NAME).first(), this.playerContainerService::getItemsContainer, this.messageService.getMessage(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_LORE).toArray()));
		pane.addItem(createContainerIcon(this.messageService.getMessage(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_NAME).first(), this.playerContainerService::getRewardsContainer, this.messageService.getMessage(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_LORE).toArray()));
		
		return pane;
	}
	
	private GuiItem createContainerIcon(String name, Function<UUID, PlayerContainerGUI> containerGetter, String... description) 
	{
		ItemStack item = new ItemBuilder(Material.CHEST)
				.named(name)
				.withLore(description)
				.createCopy();
		
		return new GuiItem(item, event -> 
		{
			Player employer = (Player) event.getWhoClicked();
			
			containerGetter.apply(employer.getUniqueId()).show(employer);
		});
	}
}