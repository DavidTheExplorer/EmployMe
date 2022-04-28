package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.INVENTORY_PLAYER_JOBS_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createWalls;

import java.util.List;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;

public class PlayerJobsGUI extends ChestGui
{
	private final List<Job> jobsToDisplay;
	private final MessageService messageService;
	
	public PlayerJobsGUI(JobBoardGUI jobBoardGUI, MessageService messageService, List<Job> jobsToDisplay)
	{
		super(6, messageService.getMessage(INVENTORY_PLAYER_JOBS_TITLE).first());
		
		this.jobsToDisplay = jobsToDisplay;
		this.messageService = messageService;
		
		setOnTopClick(event -> event.setCancelled(true));
		setOnClose(event -> jobBoardGUI.show(event.getPlayer()));
		addPane(createWalls(this, Priority.LOWEST));
		addPane(createJobsPane());
		
		update();
	}
	
	private Pane createJobsPane() 
	{
		OutlinePane pane = new OutlinePane(1, 1, 7, 5, Priority.LOW);
		pane.setOrientation(HORIZONTAL);

		this.jobsToDisplay.stream()
		.map(job -> JobIconFactory.create(job, this.messageService))
		.map(GuiItem::new)
		.forEach(pane::addItem);

		return pane;
	}
}