package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.conversations.Conversations;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.items.ItemBuilder;

public class JobCreationGUI extends ChestGui
{
	private final Conversations conversations;
	private final JobBoard jobBoard;
	private final MessageService messageService;
	private final PlayerContainerService playerContainerService;
	
	public JobCreationGUI(Conversations conversations, JobBoard jobBoard, MessageService messageService, PlayerContainerService playerContainerService)
	{
		super(3, "Create a new Job");
		
		this.conversations = conversations;
		this.jobBoard = jobBoard;
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
		
		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createOptionsPane(Priority.LOW));
		update();
	}
	
	private OutlinePane createOptionsPane(Priority priority) 
	{
		OutlinePane pane = new OutlinePane(2, 1, 6, 1, priority);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(3);
		
		pane.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT)
				.named(GOLD + "Money Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay a certain amount of money.")
				.createCopy(), 
				event -> 
		{
			Player player = (Player) event.getWhoClicked();
			
			player.closeInventory();
			this.conversations.ofMoneyJobCreation(player).begin();
		}));
		
		pane.addItem(new GuiItem(new ItemBuilder(Material.CHEST)
				.named(AQUA + "Items Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy(),
				event -> new ItemsRewardOfferGUI(this.jobBoard, this.messageService, this.playerContainerService, this.conversations).show(event.getWhoClicked())));
		
		return pane;
	}
}
