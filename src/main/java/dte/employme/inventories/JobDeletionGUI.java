package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_DELETED;
import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.items.JobBasicIcon;
import dte.employme.job.Job;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.InventoryFrameworkUtils;
import dte.employme.utils.items.ItemBuilder;

public class JobDeletionGUI extends ChestGui
{
	private final JobBoard jobBoard;
	private final List<Job> jobsToDisplay;
	private final MessageService messageService;

	public JobDeletionGUI(JobBoard jobBoard, List<Job> jobsToDisplay, MessageService messageService) 
	{
		super(6, "Select Jobs to Delete");

		this.jobBoard = jobBoard;
		this.jobsToDisplay = jobsToDisplay;
		this.messageService = messageService;

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createJobsPane(Priority.LOW));
		addPane(InventoryFrameworkUtils.createRectangle(Priority.LOWEST, 0, 0, 9, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		update();
	}

	private OutlinePane createJobsPane(Priority priority) 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, priority);
		pane.setOrientation(HORIZONTAL);

		this.jobsToDisplay.stream()
		.map(this::createDeletionIcon)
		.forEach(pane::addItem);

		return pane;
	}

	private GuiItem createDeletionIcon(Job job) 
	{
		ItemStack item = new ItemBuilder(JobBasicIcon.of(job))
				.addToLore(true,
						createSeparationLine(GRAY, 23),
						bold(DARK_RED) + "Click to Delete!",
						createSeparationLine(GRAY, 23))
				.createCopy();

		return new GuiItem(item, event -> 
		{
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();
			this.jobBoard.removeJob(job);
			job.getReward().giveTo(job.getEmployer());

			player.sendMessage(this.messageService.getMessage(JOB_SUCCESSFULLY_DELETED));
		});
	}
}