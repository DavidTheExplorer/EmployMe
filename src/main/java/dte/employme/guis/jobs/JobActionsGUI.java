package dte.employme.guis.jobs;

import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_COMPLETION_ITEM_DESCRIPTION;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_COMPLETION_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_DELETE_JOB_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_ITEMS_REWARD_PREVIEW_ITEM_DESCRIPTION;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_ITEMS_REWARD_PREVIEW_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_JOB_UNAVAILABLE;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_NOT_COMPLETED_ITEM_DESCRIPTION;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_NOT_COMPLETED_ITEM_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_TITLE;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_TRACKER_ITEM_DESCRIPTION;
import static dte.employme.messages.MessageKey.GUI_JOB_ACTIONS_TRACKER_ITEM_NAME;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createItemPane;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createSquare;

import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobPartialCompletionAmountPrompt;
import dte.employme.job.Job;
import dte.employme.messages.MessageKey;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.PartialReward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.PartialCompletionInfo;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;

public class JobActionsGUI extends ChestGui
{
	private final Job job;
	private final JobBoard jobBoard;
	private final Player player;
	private final JobService jobService;
	private final MessageService messageService;

	private boolean openParentOnExit;

	public JobActionsGUI(Job job, JobBoard jobBoard, Player player, Gui openOnClose, MessageService messageService, JobService jobService)
	{
		super(3, messageService.loadMessage(GUI_JOB_ACTIONS_TITLE).first());
		
		this.job = job;
		this.jobBoard = jobBoard;
		this.player = player;
		this.jobService = jobService;
		this.messageService = messageService;
		this.openParentOnExit = (openOnClose != null);

		//add completion area
		addPane(createSquare(Priority.LOWEST, 0, 0, 3, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createItemPane(1, 1, createCompletionItem()));

		//add options panel
		addPane(createRectangle(Priority.LOWEST, 3, 0, 6, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createRectangle(Priority.LOWEST, 4, 1, 4, 1, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		addPane(createOptionsPane());

		//register listeners
		setOnTopClick(event -> event.setCancelled(true));

		setOnClose(event ->
		{
			if(this.openParentOnExit)
				openOnClose.show(this.player);
		});
	}

	private Pane createOptionsPane() 
	{
		//TODO: refactor
		boolean itemsReward = this.job.getReward() instanceof ItemsReward;
		boolean canDelete = this.player.hasPermission("employme.admin.delete") || this.job.getEmployer().equals(this.player);

		int optionsAvailable = (int) Stream.of(itemsReward, canDelete)
				.filter(option -> option)
				.count();

		OutlinePane pane = new OutlinePane(7 - optionsAvailable, 1, 9, 1);

		if(itemsReward)
			pane.addItem(createItemsRewardPreviewItem());

		if(canDelete)
			pane.addItem(createDeletionItem());

		pane.addItem(createTrackingItem());

		return pane;
	}

	private GuiItem createTrackingItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.COMPASS)
						.named(this.messageService.loadMessage(GUI_JOB_ACTIONS_TRACKER_ITEM_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_JOB_ACTIONS_TRACKER_ITEM_DESCRIPTION).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					if(!checkJobAvailability())
						return;
					
					this.openParentOnExit = false;

					this.player.closeInventory();
					this.jobService.startLiveUpdates(this.player, this.job);
				})
				.build();
	}

	private GuiItem createItemsRewardPreviewItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.CHEST)
						.named(this.messageService.loadMessage(GUI_JOB_ACTIONS_ITEMS_REWARD_PREVIEW_ITEM_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_JOB_ACTIONS_ITEMS_REWARD_PREVIEW_ITEM_DESCRIPTION).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					if(!checkJobAvailability())
						return;
					
					this.openParentOnExit = false;
					new ItemsRewardPreviewGUI(this.player, this, (ItemsReward) this.job.getReward(), this.messageService).show(this.player);
				})
				.build();
	}

	private GuiItem createDeletionItem() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.BARRIER)
						.named(this.messageService.loadMessage(GUI_JOB_ACTIONS_DELETE_JOB_ITEM_NAME).first())
						.withLore(this.messageService.loadMessage(MessageKey.GUI_JOB_ACTIONS_DELETE_JOB_ITEM_DESCRIPTION).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					if(!checkJobAvailability())
						return;
					
					this.openParentOnExit = false;

					this.player.closeInventory();
					this.jobBoard.removeJob(this.job);
					this.job.getReward().giveTo(this.job.getEmployer());
					this.messageService.loadMessage(JOB_SUCCESSFULLY_CANCELLED).sendTo(this.player);
				})
				.build();
	}

	private GuiItem createCompletionItem() 
	{
		boolean finishedJob = this.jobService.getFinishState(this.player, this.job).hasFinished();
		
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(finishedJob ? Material.LIME_TERRACOTTA : Material.RED_TERRACOTTA)
						.named(this.messageService.loadMessage(finishedJob ? GUI_JOB_ACTIONS_COMPLETION_ITEM_NAME : GUI_JOB_ACTIONS_NOT_COMPLETED_ITEM_NAME).first())
						.withLore(this.messageService.loadMessage(finishedJob ? GUI_JOB_ACTIONS_COMPLETION_ITEM_DESCRIPTION : GUI_JOB_ACTIONS_NOT_COMPLETED_ITEM_DESCRIPTION).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					if(!checkJobAvailability())
						return;
					
					//do nothing if the player didn't finish the job
					if(!finishedJob)
						return;

					this.openParentOnExit = false;
					this.player.closeInventory();

					if(this.job.getReward() instanceof PartialReward)
						askGoalAmount(this.job).begin();
					else
						completeJob(this.job, JobCompletionContext.normal(this.job));
				})
				.build();
	}

	private Conversation askGoalAmount(Job job) 
	{
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new JobPartialCompletionAmountPrompt(this.messageService, this.jobService, job))
				.addConversationAbandonedListener(abandonedEvent -> 
				{
					if(!abandonedEvent.gracefulExit())
						return;

					int amountToUse = (int) abandonedEvent.getContext().getSessionData("Amount To Use");
					completeJob(job, (amountToUse == job.getGoal().getAmount()) ? JobCompletionContext.normal(job) : JobCompletionContext.partial(this.jobService.getPartialCompletionInfo(this.player, job, amountToUse)));
				})
				.buildConversation(this.player);
	}

	private void completeJob(Job job, JobCompletionContext context) 
	{
		if(!checkJobAvailability())
			return;
		
		this.jobBoard.completeJob(job, this.player, context);

		if(!context.isJobCompleted())
			updatePartialJob(job, context.getPartialInfo());
	}

	private void updatePartialJob(Job job, PartialCompletionInfo partialCompletionInfo) 
	{
		ItemStack newGoal = new ItemBuilder(job.getGoal())
				.amounted(job.getGoal().getAmount() - partialCompletionInfo.getGoal().getAmount())
				.createCopy();

		PartialReward newReward = ((PartialReward) job.getReward()).afterPartialCompletion(partialCompletionInfo.getPercentage());

		job.setGoal(newGoal, job.getGoalProvider());
		job.setReward(newReward);
	}

	//fix exploit where if 2 players have the board open, both can complete the same job - in order to duplicate the reward
	private boolean checkJobAvailability() 
	{
		if(this.jobBoard.containsJob(this.job)) 
			return true;

		this.player.closeInventory();
		this.messageService.loadMessage(GUI_JOB_ACTIONS_JOB_UNAVAILABLE).sendTo(this.player);
		return false;
	}
}
