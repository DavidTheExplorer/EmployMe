package dte.employme.guis.jobs;

import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_NEXT_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_NEXT_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_OFFER_COMPLETED;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_OFFER_NOT_COMPLETED;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_OFFER_PARTIALLY_COMPLETED;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PREVIOUS_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PREVIOUS_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_TITLE;
import static dte.employme.services.job.JobService.FinishState.NEGATIVE;
import static dte.employme.services.job.JobService.FinishState.PARTIALLY;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createWalls;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonBuilder;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import dte.employme.items.JobIcon;
import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.services.job.JobService;
import dte.employme.services.job.JobService.FinishState;
import dte.employme.services.message.MessageService;
import dte.employme.utils.CenteredMessage;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;
import dte.employme.utils.items.ItemBuilder;

public class JobBoardGUI extends ChestGui
{
	private final Player player;
	private final JobBoard jobBoard;
	private final JobService jobService;
	private final MessageService messageService;
	private final PaginatedPane jobsPane;

	public JobBoardGUI(Player player, JobBoard jobBoard, JobService jobService, MessageService messageService)
	{
		super(6, messageService.loadMessage(GUI_JOB_BOARD_TITLE).first());

		this.player = player;
		this.jobBoard = jobBoard;
		this.jobService = jobService;
		this.messageService = messageService;
		
		this.jobsPane = new PaginatedPane(1, 1, 7, 4, Priority.LOWEST);
		this.jobsPane.addPane(0, createPage(this.jobsPane));
		this.jobBoard.getOfferedJobs().forEach(this::addJob);

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createWalls(this, Priority.LOWEST));
		addPane(createPanel());
		addPane(this.jobsPane);
	}
	
	public void addJob(Job job) 
	{
		InventoryFrameworkUtils.addItem(createOfferIcon(job), this.jobsPane, this);
	}
	
	private Pane createPanel() 
	{
		OutlinePane panel = new OutlinePane(1, 5, 9, 1, Priority.LOW);
		panel.setGap(2);
		
		panel.addItem(new GuiItemBuilder()
				.forItem(backButtonBuilder()
						.named(this.messageService.loadMessage(GUI_JOB_BOARD_PREVIOUS_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_JOB_BOARD_PREVIOUS_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(backButtonListener(this, this.jobsPane))
				.build());
		
		panel.addItem(createPersonalJobsItem());
		
		panel.addItem(new GuiItemBuilder()
				.forItem(nextButtonBuilder()
						.named(this.messageService.loadMessage(GUI_JOB_BOARD_NEXT_PAGE_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_JOB_BOARD_NEXT_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(nextButtonListener(this, this.jobsPane))
				.build());
		
		return panel;
	}

	private GuiItem createOfferIcon(Job job)
	{
		return new GuiItemBuilder()
				.forItem(createOfferIconItem(job))
				.whenClicked(event -> new JobActionsGUI(job, this.jobBoard, this.player, this, this.messageService, this.jobService).show(this.player))
				.build();
	}

	private GuiItem createPersonalJobsItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.PLAYER_HEAD)
						.named(this.messageService.loadMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME).first())
						.withItemMeta(SkullMeta.class, meta -> meta.setOwningPlayer(this.player))
						.withLore(this.messageService.loadMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					List<Job> playerJobs = this.jobBoard.getJobsOfferedBy(this.player.getUniqueId());

					new PlayerJobsGUI(playerJobs, this.jobBoard, this.messageService, this.jobService).show(this.player);
				})
				.build();
	}

	public MessageBuilder getJobStatusMessage(Job job, FinishState finishState) 
	{
		if(finishState == NEGATIVE) 
			return this.messageService.loadMessage(GUI_JOB_BOARD_OFFER_NOT_COMPLETED);

		return this.messageService.loadMessage((finishState == PARTIALLY ? GUI_JOB_BOARD_OFFER_PARTIALLY_COMPLETED : GUI_JOB_BOARD_OFFER_COMPLETED))
				.inject("goal amount", this.jobService.getGoalAmountInInventory(job, this.player.getInventory()));
	}

	private ItemStack createOfferIconItem(Job job) 
	{
		ItemStack basicIcon = JobIcon.create(job, this.messageService);
		FinishState currentState = this.jobService.getFinishState(this.player, job);

		//add the status and ID to the lore
		List<String> statusMessage = getJobStatusMessage(job, currentState).toList();

		int separatorLength = statusMessage.stream()
				.map(ChatColor::stripColor)
				.mapToInt(String::length)
				.max()
				.getAsInt();

		String separator = createSeparationLine(currentState.hasFinished() ? WHITE : DARK_RED, separatorLength);

		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(" ");
		lore.addAll(this.messageService.loadMessage(MessageKey.JOB_ICON_VIEW_ACTIONS_DESCRIPTION).toList());
		lore.add(" ");
		lore.add(separator);
		statusMessage.stream().map(line -> CenteredMessage.of(line, separator)).forEach(lore::add);
		lore.add(separator);

		return new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();
	}
}