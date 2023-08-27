package dte.employme.guis.addnotifiers;

import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;

import dte.employme.configs.GuiConfig;
import dte.employme.job.addnotifiers.JobAddNotifier;
import dte.employme.services.job.addnotifiers.JobAddNotifierService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;

public class JobAddNotifiersGUIFactory
{
	private final GuiConfig config;
	private final JobAddNotifier defaultNotifier;
	private final JobAddNotifierService jobAddNotifierService;
	private final MessageService messageService;

	public JobAddNotifiersGUIFactory(GuiConfig config, JobAddNotifier defaultNotifier, JobAddNotifierService jobAddNotifierService, MessageService messageService) 
	{
		this.config = config;
		this.defaultNotifier = defaultNotifier;
		this.jobAddNotifierService = jobAddNotifierService;
		this.messageService = messageService;
	}

	public ChestGui create(Player viewer) 
	{
		ChestGui gui = new ChestGui(3, this.config.getTitle());
		
		//add panes
		gui.addPane(parseBackground());
		gui.addPane(parseNotifiersPane(viewer.getUniqueId()));

		return gui;
	}

	private OutlinePane parseBackground()
	{
		OutlinePane pane = new OutlinePane(Slot.fromIndex(0), 9, 3, Priority.LOWEST);
		pane.setRepeat(true);
		pane.addItem(this.config.parseGuiItem("background").build());

		return pane;
	}

	private Pane parseNotifiersPane(UUID viewerUUID)
	{
		OutlinePane pane = new OutlinePane(2, 1, 5, 1, Priority.LOW);
		pane.setGap(1);

		pane.addItem(createNotifierIcon(viewerUUID, "All Jobs"));
		pane.addItem(createNotifierIcon(viewerUUID, "Material Subscriptions"));
		pane.addItem(createNotifierIcon(viewerUUID, "None"));

		return pane;
	}

	private GuiItem createNotifierIcon(UUID viewerUUID, String notifierName)
	{
		JobAddNotifier notifier = this.jobAddNotifierService.getByName(notifierName);
		
		ItemStack baseIcon = this.config.parseGuiItem(notifier.getName().toLowerCase().replace(' ', '-')).build().getItem();

		return new GuiItemBuilder()
				.forItem(new ItemBuilder(baseIcon)
						.withLore(parseNotifierLore(viewerUUID, baseIcon, notifier))
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					this.jobAddNotifierService.setPlayerNotifier(player.getUniqueId(), notifier);
					
					this.messageService.loadMessage(YOUR_NEW_JOB_ADDED_NOTIFIER_IS)
					.inject("job added notifier", notifier.getName())
					.sendTo(player);
				})
				.build();
	}
	
	private String[] parseNotifierLore(UUID viewerUUID, ItemStack baseIcon, JobAddNotifier notifier)
	{
		List<String> lore = new ArrayList<>(baseIcon.getItemMeta().getLore());

		if(this.jobAddNotifierService.getPlayerNotifier(viewerUUID, this.defaultNotifier).equals(notifier))
		{
			lore.add(" ");
			lore.addAll(this.config.getText("currently-selected").toList());
		}
		
		return lore.toArray(new String[0]);
	}
}
