package dte.employme.inventories;

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
import static dte.employme.utils.InventoryFrameworkUtils.backButtonBuilder;
import static dte.employme.utils.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.InventoryFrameworkUtils.createPage;
import static dte.employme.utils.InventoryFrameworkUtils.createWalls;
import static dte.employme.utils.InventoryFrameworkUtils.nextButtonBuilder;
import static dte.employme.utils.InventoryFrameworkUtils.nextButtonListener;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.GuiItemBuilder;
import dte.employme.utils.InventoryFrameworkUtils;
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
		super(6, messageService.getMessage(GUI_JOB_BOARD_TITLE).first());

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
		boolean finished = this.jobService.hasFinished(this.player, job);

		//add the status and ID to the lore
		String separator = createSeparationLine(finished ? WHITE : DARK_RED, finished ? 25 : 29);
		String finishMessage = this.messageService.getMessage(finished ? GUI_JOB_BOARD_OFFER_COMPLETED : GUI_JOB_BOARD_OFFER_NOT_COMPLETED).first();

		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(separator);
		lore.add(StringUtils.repeat(" ", finished ? 8 : 4) + finishMessage);
		lore.add(separator);

		ItemStack item = new ItemBuilder(basicIcon)
				.withLore(lore.toArray(new String[0]))
				.createCopy();

		return new GuiItem(item, event -> 
		{
			//Right click = preview mode for jobs that offer items
			if(event.isRightClick() && job.getReward() instanceof ItemsReward)
			{
				ItemsRewardPreviewGUI gui = new ItemsRewardPreviewGUI((ItemsReward) job.getReward(), this.messageService);
				gui.setOnClose(closeEvent -> this.player.openInventory(event.getInventory()));
				gui.show(this.player);
			}

			//the user wants to finish the job
			else if(this.jobService.hasFinished(this.player, job))
			{
				this.player.closeInventory();
				this.jobBoard.completeJob(job, this.player);
			}
		});
	}
	
	private GuiItem createPersonalJobsItem() 
	{
		ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
				.named(this.messageService.getMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME).first())
				.withItemMeta(SkullMeta.class, meta -> meta.setOwningPlayer(this.player))
				.withLore(this.messageService.getMessage(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE).toArray())
				.createCopy();
		
		return new GuiItem(item, event -> 
		{
			List<Job> playerJobs = this.jobBoard.getJobsOfferedBy(this.player.getUniqueId());
			
			new PlayerJobsGUI(this, this.messageService, playerJobs).show(this.player);
		});
	}
}