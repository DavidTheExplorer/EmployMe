package dte.employme.guis.board;

import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

import java.util.List;

import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.configs.GuiConfig;
import dte.employme.items.JobIcon;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;

/**
 * A factory class that creates the GUI that displays all jobs offered by the viewer.
 * 
 * @apiNote Due to a circular dependency with {@link JobBoardGUIFactory}, the <i>init</i> method must be called after construction.
 */
public class PlayerJobsGUIFactory
{
	private final GuiConfig config;
	private final JobBoard globalBoard;
	private final MessageService messageService;
	private final JobActionsGUIFactory jobActionsGUIFactory;
	private JobBoardGUIFactory jobBoardGUIFactory;

	public PlayerJobsGUIFactory(GuiConfig config, JobBoard globalBoard, JobActionsGUIFactory jobActionsGUIFactory, MessageService messageService) 
	{
		this.config = config;
		this.globalBoard = globalBoard;
		this.jobActionsGUIFactory = jobActionsGUIFactory;
		this.messageService = messageService;
	}

	public void init(JobBoardGUIFactory jobBoardGUIFactory) 
	{
		this.jobBoardGUIFactory = jobBoardGUIFactory;
	}

	public ChestGui create(Player viewer)
	{
		PlayerJobsGUI gui = new PlayerJobsGUI(6, this.config.getTitle());

		List<Job> viewerJobs = this.globalBoard.getJobsOfferedBy(viewer.getUniqueId());

		//add panes
		PaginatedPane jobsPane = createJobsPane(viewer, gui, viewerJobs);

		gui.addPane(createPanel(gui, jobsPane));
		gui.addPane(parsePanelBackground());
		gui.addPane(jobsPane);

		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));

		//return to the job board on exit
		gui.setOnClose(event -> 
		{
			if(!gui.exitedByESC)
				return;

			this.jobBoardGUIFactory.create(viewer, this.globalBoard).show(viewer);
		});

		return gui;
	}

	private PaginatedPane createJobsPane(Player viewer, PlayerJobsGUI gui, List<Job> jobsToDisplay) 
	{
		PaginatedPane pages = new PaginatedPane(0, 0, 9, 5, Priority.LOWEST);
		pages.addPane(0, createPage(pages));

		jobsToDisplay.stream()
		.map(job -> parseJobIcon(viewer, gui, job))
		.forEach(guiItem -> InventoryFrameworkUtils.addItem(guiItem, pages, gui));

		return pages;
	}

	private Pane createPanel(PlayerJobsGUI gui, PaginatedPane jobsPane) 
	{
		OutlinePane panel = new OutlinePane(2, 5, 9, 1, Priority.LOW);
		panel.setGap(3);

		this.config.parseGuiItem("back")
		.whenClicked(backButtonListener(gui, jobsPane))
		.addTo(panel);

		this.config.parseGuiItem("next")
		.whenClicked(backButtonListener(gui, jobsPane))
		.addTo(panel);

		return panel;
	}

	private Pane parsePanelBackground() 
	{
		return createRectangle(Priority.LOWEST, 0, 5, 9, 1, this.config.parseGuiItem("background").build());
	}

	private GuiItem parseJobIcon(Player viewer, PlayerJobsGUI gui, Job job) 
	{
		return new GuiItemBuilder()
				.forItem(JobIcon.create(job, this.messageService))
				.whenClicked(event -> 
				{
					gui.exitedByESC = false;

					this.jobActionsGUIFactory.create(viewer, job).show(viewer);
				})
				.build();
	}



	public class PlayerJobsGUI extends ChestGui
	{
		boolean exitedByESC = true;

		public PlayerJobsGUI(int rows, String title)
		{
			super(rows, title);
		}
	}
}
