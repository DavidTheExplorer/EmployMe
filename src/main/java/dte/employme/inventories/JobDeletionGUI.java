package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_DELETION_DELETE_INSTRUCTION;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_DELETION_TITLE;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_DELETED;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.GRAY;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.items.ItemBuilder;

public class JobDeletionGUI extends ChestGui
{
	private final JobBoard jobBoard;
	private final List<Job> jobsToDisplay;
	private final MessageService messageService;
	private final JobRewardService jobRewardService;

	public JobDeletionGUI(JobBoard jobBoard, List<Job> jobsToDisplay, MessageService messageService, JobRewardService jobRewardService) 
	{
		super(6, messageService.getMessage(INVENTORY_JOB_DELETION_TITLE).first());

		this.jobBoard = jobBoard;
		this.jobsToDisplay = jobsToDisplay;
		this.messageService = messageService;
		this.jobRewardService = jobRewardService;

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createJobsPane());
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 6, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		update();
	}

	private Pane createJobsPane() 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, Priority.LOW);
		pane.setOrientation(HORIZONTAL);

		this.jobsToDisplay.stream()
		.map(this::createDeletionIcon)
		.forEach(pane::addItem);

		return pane;
	}

	private GuiItem createDeletionIcon(Job job) 
	{
		ItemStack item = new ItemBuilder(JobIconFactory.create(job, this.messageService))
				.addToLore(true,
						createSeparationLine(GRAY, 23),
						this.messageService.getMessage(INVENTORY_JOB_DELETION_DELETE_INSTRUCTION).first(),
						createSeparationLine(GRAY, 23))
				.createCopy();

		return new GuiItem(item, event -> 
		{
			Player player = (Player) event.getWhoClicked();

			//Right click = preview mode for jobs that offer items
			if(event.isRightClick() && job.getReward() instanceof ItemsReward)
			{
				ItemsRewardPreviewGUI gui = new ItemsRewardPreviewGUI((ItemsReward) job.getReward(), this.messageService);
				gui.setOnClose(closeEvent -> show(player));
				gui.show(player);
			}
			
			//delete the job
			else 
			{
				player.closeInventory();
				this.jobBoard.removeJob(job);
				this.jobRewardService.refund(job.getEmployer(), job.getReward());
				this.messageService.getMessage(JOB_SUCCESSFULLY_DELETED).sendTo(player);
			}
		});
	}
}