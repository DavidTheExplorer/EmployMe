package dte.employme.guis;

import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_NEXT_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_NEXT_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_OFFER_COMPLETED;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_OFFER_NOT_COMPLETED;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PREVIOUS_PAGE_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PREVIOUS_PAGE_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_TITLE;
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
import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.InventoryFrameworkUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.StringUtils;

public class JobBoardGUI extends ChestGui
{
	private final Player player;
	private final JobBoard jobBoard;
	private final MessageService messageService;
	
	private final PaginatedPane jobsPane;

	public JobBoardGUI(Player player, JobBoard jobBoard, MessageService messageService)
	{
		super(6, messageService.getMessage(GUI_JOB_BOARD_TITLE).first());

		this.player = player;
		this.jobBoard = jobBoard;
		this.messageService = messageService;
		
		this.jobsPane = new PaginatedPane(1, 1, 7, 4, Priority.LOWEST);
		this.jobsPane.addPane(0, createPage(this.jobsPane));
		this.jobBoard.getOfferedJobs().forEach(this::addJob);

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createWalls(this, Priority.LOWEST));
		addPane(createPanel());
		addPane(this.jobsPane);
		update();
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
						.named(this.messageService.getMessage(GUI_JOB_BOARD_PREVIOUS_PAGE_NAME).first())
						.withLore(this.messageService.getMessage(GUI_JOB_BOARD_PREVIOUS_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(backButtonListener(this, this.jobsPane))
				.build());
		
		panel.addItem(createPersonalJobsItem());
		
		panel.addItem(new GuiItemBuilder()
				.forItem(nextButtonBuilder()
						.named(this.messageService.getMessage(GUI_JOB_BOARD_NEXT_PAGE_NAME).first())
						.withLore(this.messageService.getMessage(GUI_JOB_BOARD_NEXT_PAGE_LORE).toArray())
						.createCopy())
				.whenClicked(nextButtonListener(this, this.jobsPane))
				.build());
		
		return panel;
	}
	
	private GuiItem createOfferIcon(Job job) 
	{
		ItemStack basicIcon = JobIconFactory.create(job, this.messageService);
		boolean finished = job.hasFinished(this.player);

		//add the status and ID to the lore
		String separator = createSeparationLine(finished ? WHITE : DARK_RED, finished ? 25 : 29);
		String finishMessage = this.messageService.getMessage(finished ? GUI_JOB_BOARD_OFFER_COMPLETED : GUI_JOB_BOARD_OFFER_NOT_COMPLETED).first();

		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(separator);
		lore.add(StringUtils.repeat(" ", finished ? 8 : 4) + finishMessage);
		lore.add(separator);

		return new GuiItemBuilder()
				.forItem(new ItemBuilder(basicIcon)
						.withLore(lore.toArray(new String[0]))
						.createCopy())
				.whenClicked(event -> 
				{
					//Right click = preview mode for jobs that offer items
					if(event.isRightClick() && job.getReward() instanceof ItemsReward)
					{
						new ItemsRewardPreviewGUI(this.player, this, (ItemsReward) job.getReward(), this.messageService).show(this.player);
					}

					//the user wants to finish the job
					else if(job.hasFinished(this.player))
					{
						this.player.closeInventory();
						this.jobBoard.completeJob(job, this.player);
					}
				})
				.build();
	}

	private GuiItem createPersonalJobsItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.PLAYER_HEAD)
						.named(this.messageService.getMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME).first())
						.withItemMeta(SkullMeta.class, meta -> meta.setOwningPlayer(this.player))
						.withLore(this.messageService.getMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					List<Job> playerJobs = this.jobBoard.getJobsOfferedBy(this.player.getUniqueId());

					new PlayerJobsGUI(this, this.messageService, playerJobs).show(this.player);
				})
				.build();
	}
}