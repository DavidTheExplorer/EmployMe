package dte.employme.guis;

import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_JOB_NOT_CONTAINED;
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
import static dte.employme.messages.Placeholders.GOAL_AMOUNT;
import static dte.employme.services.job.JobService.FinishState.FULLY;
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
import static org.bukkit.ChatColor.stripColor;

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
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.items.JobIconFactory;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.PartialReward;
import dte.employme.rewards.Reward;
import dte.employme.services.job.JobService;
import dte.employme.services.job.JobService.FinishState;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.PartialCompletionInfo;
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
		FinishState currentState = this.jobService.getFinishState(this.player, job);

		//add the status and ID to the lore
		String statusMessage = getJobStatusMessage(job, currentState);
		String separator = createSeparationLine(currentState.hasFinished() ? WHITE : DARK_RED, stripColor(statusMessage).length());
		
		List<String> lore = basicIcon.getItemMeta().getLore();
		lore.add(separator);
		lore.add(CenteredMessage.of(statusMessage, separator));
		lore.add(separator);

		return new GuiItemBuilder()
				.forItem(new ItemBuilder(basicIcon)
						.withLore(lore.toArray(new String[0]))
						.createCopy())
				.whenClicked(event -> 
				{
					//fix dangerous exploit where if 2 players have the board open, both can complete the same job - in order to dupe the reward
					if(!this.jobBoard.containsJob(job)) 
					{
						this.messageService.getMessage(GUI_JOB_BOARD_JOB_NOT_CONTAINED).sendTo(this.player);
						this.player.closeInventory();
						return;
					}
					
					Reward reward = job.getReward();
					
					//Right click = preview mode for jobs that offer items
					if(event.isRightClick() && reward instanceof ItemsReward)
					{
						new ItemsRewardPreviewGUI(this.player, this, (ItemsReward) reward, this.messageService).show(this.player);
						return;
					}

					//the user wants to finish the job
					FinishState finishState = this.jobService.getFinishState(this.player, job);

					if(!finishState.hasFinished())
						return;

					JobCompletionContext context = createCompletionContext(job, finishState);

					this.player.closeInventory();
					this.jobBoard.completeJob(job, this.player, context);

					if(!context.isJobCompleted())
						updatePartialJob(job, context.getPartialInfo());
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

	public String getJobStatusMessage(Job job, FinishState finishState) 
	{
		if(finishState == FinishState.NEGATIVE) 
			return this.messageService.getMessage(GUI_JOB_BOARD_OFFER_NOT_COMPLETED).first();
		
		return this.messageService.getMessage((finishState == PARTIALLY ? GUI_JOB_BOARD_OFFER_PARTIALLY_COMPLETED : GUI_JOB_BOARD_OFFER_COMPLETED))
				.inject(GOAL_AMOUNT, createCompletionContext(job, finishState).getGoal().getAmount())
				.first();
	}

	private JobCompletionContext createCompletionContext(Job job, FinishState finishState) 
	{
		return finishState == FULLY ? JobCompletionContext.normal(job) : JobCompletionContext.partial(this.jobService.getPartialCompletionInfo(this.player, job));
	}

	private void updatePartialJob(Job job, PartialCompletionInfo partialCompletionInfo) 
	{
		ItemStack newGoal = new ItemBuilder(job.getGoal())
				.amounted(job.getGoal().getAmount() - partialCompletionInfo.getGoal().getAmount())
				.createCopy();

		PartialReward newReward = ((PartialReward) job.getReward()).afterPartialCompletion(partialCompletionInfo.getPercentage());

		job.setGoal(newGoal);
		job.setReward(newReward);
	}
}