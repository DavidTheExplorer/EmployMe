package dte.employme.inventories;

import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_TITLE;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;
import static dte.employme.messages.Placeholders.JOB_ADDED_NOTIFIER;
import static dte.employme.utils.InventoryFrameworkUtils.createWalls;
import static dte.employme.utils.InventoryUtils.createWall;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.google.common.collect.Lists;

import dte.employme.addednotifiers.JobAddedNotifier;
import dte.employme.messages.MessageKey;
import dte.employme.services.addnotifiers.JobAddedNotifierService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.items.ItemBuilder;

public class JobAddNotifiersGUI extends ChestGui
{
	private final JobAddedNotifierService jobAddedNotifierService;
	private final MessageService messageService;

	public JobAddNotifiersGUI(JobAddedNotifierService jobAddedNotifierService, MessageService messageService, UUID playerUUID)
	{
		super(3, messageService.getMessage(INVENTORY_JOB_ADDED_NOTIFIERS_TITLE).first());

		this.jobAddedNotifierService = jobAddedNotifierService;
		this.messageService = messageService;

		setOnGlobalClick(event -> event.setCancelled(true));
		addPane(createWalls(this, Priority.LOWEST));
		addPane(createBackground());
		addPane(createNotifiersPane(playerUUID));
		update();
	}

	private Pane createBackground()
	{
		OutlinePane pane = new OutlinePane(1, 1, 7, 1, Priority.LOWEST);
		pane.setGap(1);
		pane.setRepeat(true);
		pane.addItem(new GuiItem(createWall(Material.PURPLE_STAINED_GLASS_PANE)));

		return pane;
	}

	private Pane createNotifiersPane(UUID playerUUID)
	{
		OutlinePane pane = new OutlinePane(2, 1, 5, 1, Priority.LOW);
		pane.setGap(1);

		JobAddedNotifier 
		allJobsNotifier = this.jobAddedNotifierService.getByName("All Jobs"),
		subscriptionsNotifier = this.jobAddedNotifierService.getByName("Material Subscriptions"),
		noneNotifier = this.jobAddedNotifierService.getByName("None");

		pane.addItem(createNotifierIcon(allJobsNotifier, INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME, playerUUID, Material.NETHER_STAR, this.messageService.getMessage(INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE).toArray()));
		pane.addItem(createNotifierIcon(subscriptionsNotifier, INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME, playerUUID, Material.PAPER, this.messageService.getMessage(INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE).toArray()));
		pane.addItem(createNotifierIcon(noneNotifier, INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME, playerUUID, Material.BARRIER, this.messageService.getMessage(INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE).toArray()));
		
		return pane;
	}
	
	private GuiItem createNotifierIcon(JobAddedNotifier notifier, MessageKey notifierItemNameKey, UUID playerUUID, Material material, String... description)
	{
		List<String> nicerDescription = Lists.newArrayList(description);

		if(this.jobAddedNotifierService.getPlayerNotifier(playerUUID).equals(notifier)) 
			nicerDescription.addAll(Arrays.asList(" ", this.messageService.getMessage(MessageKey.INVENTORY_JOB_ADDED_NOTIFIERS_SELECTED).first()));

		ItemStack icon = new ItemBuilder(material)
				.named(this.messageService.getMessage(notifierItemNameKey).first())
				.withLore(nicerDescription.toArray(new String[0]))
				.createCopy();

		return new GuiItem(icon, event ->
		{
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();
			
			this.jobAddedNotifierService.setPlayerNotifier(player.getUniqueId(), notifier);
			
			this.messageService.getMessage(YOUR_NEW_JOB_ADDED_NOTIFIER_IS)
			.prefixed(this.messageService.getMessage(PREFIX).first())
			.inject(JOB_ADDED_NOTIFIER, notifier.getName())
			.sendTo(player);
		});
	}
}
