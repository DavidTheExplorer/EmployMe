package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_OFFER_COMPLETED;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_TITLE;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.InventoryFrameworkUtils.createWalls;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import dte.employme.board.JobBoard;
import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.service.JobService;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.items.ItemBuilder;

public class JobBoardGUI extends ChestGui
{
	private final Player player;
	private final JobBoard jobBoard;
	private final Comparator<Job> orderComparator;
	private final JobService jobService;
	private final MessageService messageService;
	private final JobIconFactory jobIconFactory;

	public JobBoardGUI(Player player, JobBoard jobBoard, Comparator<Job> orderComparator, JobService jobService, MessageService messageService, JobIconFactory jobIconFactory)
	{
		super(6, messageService.getMessage(INVENTORY_JOB_BOARD_TITLE).first());

		this.player = player;
		this.jobBoard = jobBoard;
		this.orderComparator = orderComparator;
		this.jobService = jobService;
		this.messageService = messageService;
		this.jobIconFactory = jobIconFactory;

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createWalls(this, Priority.LOWEST));
		addPane(createJobsPane());
		addPane(createPersonalJobsPane());
		update();
	}

	private Pane createJobsPane() 
	{
		OutlinePane pane = new OutlinePane(1, 1, 7, 5, Priority.LOW);
		pane.setOrientation(HORIZONTAL);

		this.jobBoard.getOfferedJobs().stream()
		.sorted(this.orderComparator)
		.map(this::createOfferIcon)
		.forEach(pane::addItem);

		return pane;
	}
	
	private Pane createPersonalJobsPane() 
	{
		StaticPane pane = new StaticPane(0, 0, 9, 6, Priority.HIGH); //for some reason LOW/NORMAL don't work
		pane.addItem(createPersonalJobsItem(), 4, 5);
		
		return pane;
	}

	private GuiItem createOfferIcon(Job job) 
	{
		ItemStack basicIcon = this.jobIconFactory.createFor(job);
		boolean finished = this.jobService.hasFinished(this.player, job);

		//add the status and ID to the lore
		String separator = createSeparationLine(finished ? WHITE : DARK_RED, finished ? 25 : 29);
		String finishMessage = this.messageService.getMessage(finished ? INVENTORY_JOB_BOARD_OFFER_COMPLETED : INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED).first();

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
				.named(this.messageService.getMessage(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME).first())
				.withItemMeta(SkullMeta.class, meta -> meta.setOwningPlayer(this.player))
				.withLore(this.messageService.getMessage(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE).toArray())
				.createCopy();
		
		return new GuiItem(item, event -> 
		{
			List<Job> playerJobs = this.jobBoard.getJobsOfferedBy(this.player.getUniqueId());
			
			new PlayerJobsGUI(this, this.messageService, playerJobs, this.orderComparator, this.jobIconFactory).show(this.player);
		});
	}
}