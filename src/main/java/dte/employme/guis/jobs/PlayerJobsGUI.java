package dte.employme.guis.jobs;

import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_PLAYER_JOBS_TITLE;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;

import java.util.List;

import org.bukkit.Material;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.items.JobIcon;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;

public class PlayerJobsGUI extends ChestGui
{
	private final List<Job> jobsToDisplay;
	private final MessageService messageService;
	
	private final PaginatedPane jobsPane;
	
	public PlayerJobsGUI(JobBoardGUI jobBoardGUI, MessageService messageService, List<Job> jobsToDisplay)
	{
		super(6, messageService.getMessage(GUI_PLAYER_JOBS_TITLE).first());
		
		this.jobsToDisplay = jobsToDisplay;
		this.messageService = messageService;
		
		this.jobsPane = createJobsPane();
		
		setOnTopClick(event -> event.setCancelled(true));
		setOnClose(event -> jobBoardGUI.show(event.getPlayer()));
		addPane(createPanel());
		addPane(createPanelBackground());
		addPane(this.jobsPane);
		
		update();
	}
	
	private PaginatedPane createJobsPane() 
	{
		PaginatedPane pages = new PaginatedPane(0, 0, 9, 5, Priority.LOWEST);
		pages.addPane(0, createPage(pages));
		
		this.jobsToDisplay.stream()
		.map(job -> JobIcon.create(job, this.messageService))
		.map(GuiItem::new)
		.forEach(guiItem -> InventoryFrameworkUtils.addItem(guiItem, pages, this));

		return pages;
	}
	
	private Pane createPanel() 
	{
		OutlinePane panel = new OutlinePane(2, 5, 9, 1, Priority.LOW);
		panel.setGap(3);
		
		panel.addItem(new GuiItemBuilder()
				.forItem(backButtonBuilder()
						.named(this.messageService.getMessage(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME).first())
						.withLore(this.messageService.getMessage(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(backButtonListener(this, this.jobsPane))
				.build());
		
		panel.addItem(new GuiItemBuilder()
				.forItem(nextButtonBuilder()
						.named(this.messageService.getMessage(GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME).first())
						.withLore(this.messageService.getMessage(GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(nextButtonListener(this, this.jobsPane))
				.build());
		
		return panel;
	}
	
	private Pane createPanelBackground() 
	{
		return createRectangle(Priority.LOWEST, 0, 5, 9, 1, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE)));
	}
}