package dte.employme.guis.board;

import static dte.employme.messages.MessageKey.JOB_ICON_VIEW_ACTIONS_DESCRIPTION;
import static dte.employme.services.job.JobService.FinishState.NEGATIVE;
import static dte.employme.services.job.JobService.FinishState.PARTIALLY;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createWalls;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
import dte.employme.messages.MessageBuilder;
import dte.employme.services.job.JobService;
import dte.employme.services.job.JobService.FinishState;
import dte.employme.services.message.MessageService;
import dte.employme.utils.CenteredMessage;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;
import dte.employme.utils.items.ItemBuilder;

/**
 * A factory class that creates the GUI that displays the jobs of a certain board.
 * 
 * @apiNote Due to a circular dependency with {@link PlayerJobsGUIFactory}, the <i>init</i> method must be called after construction.
 */
public class JobBoardGUIFactory
{
	private final GuiConfig config;
	private final JobActionsGUIFactory jobActionsGUIFactory;
	private final MessageService messageService;
	private final JobService jobService;
	private PlayerJobsGUIFactory playerJobsGUIFactory;

	public JobBoardGUIFactory(GuiConfig config, JobActionsGUIFactory jobActionsGUIFactory, MessageService messageService, JobService jobService) 
	{
		this.config = config;
		this.jobActionsGUIFactory = jobActionsGUIFactory;
		this.messageService = messageService;
		this.jobService = jobService;
	}
	
	public void init(PlayerJobsGUIFactory playerJobsGUIFactory) 
	{
		this.playerJobsGUIFactory = playerJobsGUIFactory;
	}
	
	public ChestGui create(Player viewer, JobBoard jobBoard) 
	{
		Objects.requireNonNull(this.playerJobsGUIFactory, "JobBoardGUIFactory was not initialized!");
		
		ChestGui gui = new ChestGui(6, this.config.getTitle());
		
		//add panes
		PaginatedPane jobsPane = createJobsPane(viewer, gui, jobBoard);
		
		gui.addPane(createWalls(gui, Priority.LOWEST));
		gui.addPane(parseControlPanel(viewer, gui, jobsPane, jobBoard));
		gui.addPane(jobsPane);
		
		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		
		return gui;
	}
	
	private PaginatedPane createJobsPane(Player viewer, ChestGui gui, JobBoard jobBoard) 
	{
		PaginatedPane pane = new PaginatedPane(1, 1, 7, 4, Priority.LOWEST);
		pane.addPane(0, createPage(pane));
		
		jobBoard.getOfferedJobs().stream()
		.map(job -> parseOfferIcon(viewer, job, jobBoard))
		.forEach(jobIcon -> InventoryFrameworkUtils.addItem(jobIcon, pane, gui));
		
		return pane;
	}

	private Pane parseControlPanel(Player viewer, ChestGui jobBoardGUI, PaginatedPane jobsPane, JobBoard jobBoard) 
	{
		OutlinePane panel = new OutlinePane(1, 5, 9, 1, Priority.LOW);
		panel.setGap(2);

		this.config.parseGuiItem("back")
		.whenClicked(backButtonListener(jobBoardGUI, jobsPane))
		.addTo(panel);

		panel.addItem(parsePersonalJobsItem(viewer, jobBoard));

		this.config.parseGuiItem("next")
		.whenClicked(nextButtonListener(jobBoardGUI, jobsPane))
		.addTo(panel);

		return panel;
	}

	private GuiItem parsePersonalJobsItem(Player viewer, JobBoard jobBoard) 
	{
		ItemStack item = new ItemBuilder(this.config.parseGuiItem("your-jobs").build().getItem())
				.withItemMeta(SkullMeta.class, meta -> meta.setOwningPlayer(viewer))
				.createCopy();

		return new GuiItemBuilder()
				.forItem(item)
				.whenClicked(event -> this.playerJobsGUIFactory.create(viewer).show(viewer))
				.build();
	}

	private GuiItem parseOfferIcon(Player viewer, Job job, JobBoard jobBoard)
	{
		return new GuiItemBuilder()
				.forItem(parseOfferIconItem(viewer, job))
				.whenClicked(event -> this.jobActionsGUIFactory.create(viewer, job).show(viewer))
				.build();
	}



	private ItemStack parseOfferIconItem(Player viewer, Job job) 
	{
		ItemStack basicIcon = JobIcon.create(job, this.messageService);
		FinishState currentState = this.jobService.getFinishState(viewer, job);

		//add the status and ID to the lore
		List<String> statusMessage = getJobStatusMessage(viewer, job, currentState).toList();

		int separatorLength = statusMessage.stream()
				.map(ChatColor::stripColor)
				.mapToInt(String::length)
				.max()
				.getAsInt();

		String separator = createSeparationLine(currentState.hasFinished() ? WHITE : DARK_RED, separatorLength);

		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(" ");
		lore.addAll(this.messageService.loadMessage(JOB_ICON_VIEW_ACTIONS_DESCRIPTION).toList());
		lore.add(" ");
		lore.add(separator);
		statusMessage.stream().map(line -> CenteredMessage.of(line, separator)).forEach(lore::add);
		lore.add(separator);

		return new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}
	
	public MessageBuilder getJobStatusMessage(Player viewer, Job job, FinishState finishState) 
	{
		if(finishState == NEGATIVE) 
			return this.config.getText("offer-not-completed");
		
		return this.config.getText(finishState == PARTIALLY ? "offer-partially-completed" : "offer-completed")
				.inject("goal amount", this.jobService.getGoalAmountInInventory(job, viewer.getInventory()));
	}
}
