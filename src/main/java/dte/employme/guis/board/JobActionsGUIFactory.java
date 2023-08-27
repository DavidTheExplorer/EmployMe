package dte.employme.guis.board;

import static dte.employme.messages.MessageKey.JOB_NOT_AVAILABLE_ANYMORE;
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
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobPartialCompletionAmountPrompt;
import dte.employme.job.Job;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.PartialReward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.PartialCompletionInfo;
import dte.employme.utils.inventoryframework.RespectingChestGui;
import dte.employme.utils.items.ItemBuilder;

public class JobActionsGUIFactory
{
	private final GuiConfig config;
	private final JobBoard globalBoard;
	private final JobService jobService;
	private final MessageService messageService;
	private final ItemsRewardPreviewGUIFactory itemsRewardPreviewGUIFactory;
	
	public JobActionsGUIFactory(GuiConfig config, JobBoard globalBoard, JobService jobService, MessageService messageService, ItemsRewardPreviewGUIFactory itemsRewardPreviewGUIFactory)
	{
		this.config = config;
		this.globalBoard = globalBoard;
		this.jobService = jobService;
		this.messageService = messageService;
		this.itemsRewardPreviewGUIFactory = itemsRewardPreviewGUIFactory;
	}

	public ChestGui create(Player viewer, Job job) 
	{
		RespectingChestGui gui = new RespectingChestGui(new ChestGui(3, this.config.getTitle()));

		//add completion area
		gui.addPane(createSquare(Priority.LOWEST, 0, 0, 3, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		gui.addPane(createItemPane(1, 1, parseCompletionItem(gui, viewer, job)));

		//add options panel
		gui.addPane(createRectangle(Priority.LOWEST, 3, 0, 6, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		gui.addPane(createRectangle(Priority.LOWEST, 4, 1, 4, 1, new GuiItem(createWall(Material.WHITE_STAINED_GLASS_PANE))));
		gui.addPane(parseOptionsPane(gui, viewer, job));

		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));
		
		return gui;
	}
	
	private Pane parseOptionsPane(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		//TODO: refactor
		boolean itemsReward = job.getReward() instanceof ItemsReward;
		boolean canDelete = viewer.hasPermission("employme.admin.delete") || job.getEmployer().equals(viewer);

		int optionsAvailable = (int) Stream.of(itemsReward, canDelete)
				.filter(option -> option)
				.count();

		OutlinePane pane = new OutlinePane(7 - optionsAvailable, 1, 9, 1);

		if(itemsReward)
			pane.addItem(parseItemsRewardPreviewItem(jobActionsGUI, viewer, job));

		if(canDelete)
			pane.addItem(parseDeletionItem(jobActionsGUI, viewer, job));

		pane.addItem(parseTrackingItem(jobActionsGUI, viewer, job));

		return pane;
	}

	private GuiItem parseTrackingItem(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		return this.config.parseGuiItem("tracker")
				.whenClicked(event -> 
				{
					if(!checkJobAvailability(viewer, job))
						return;
					
					jobActionsGUI.removeParent();

					viewer.closeInventory();
					this.jobService.startLiveUpdates(viewer, job);
				})
				.build();
	}

	private GuiItem parseItemsRewardPreviewItem(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		return this.config.parseGuiItem("items-reward-preview")
				.whenClicked(event -> 
				{
					if(!checkJobAvailability(viewer, job))
						return;
					
					jobActionsGUI.removeParent();
					
					this.itemsRewardPreviewGUIFactory.create((ItemsReward) job.getReward()).show(viewer);
				})
				.build();
	}

	private GuiItem parseDeletionItem(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		return this.config.parseGuiItem("delete")
				.whenClicked(event -> 
				{
					if(!checkJobAvailability(viewer, job))
						return;
					
					jobActionsGUI.removeParent();

					viewer.closeInventory();
					this.globalBoard.removeJob(job);
					job.getReward().giveTo(job.getEmployer());
					this.messageService.loadMessage(JOB_SUCCESSFULLY_CANCELLED).sendTo(viewer);
				})
				.build();
	}

	private GuiItem parseCompletionItem(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		boolean finishedJob = this.jobService.getFinishState(viewer, job).hasFinished();
		
		return this.config.parseGuiItem(finishedJob ? "complete" : "cannot-complete")
				.whenClicked(event -> 
				{
					if(!checkJobAvailability(viewer, job))
						return;
					
					//do nothing if the player didn't finish the job
					if(!finishedJob)
						return;
					
					jobActionsGUI.removeParent();
					viewer.closeInventory();

					if(job.getReward() instanceof PartialReward)
						askGoalAmount(jobActionsGUI, viewer, job).begin();
					else
						completeJob(viewer, job, JobCompletionContext.normal(job));
				})
				.build();
	}

	private Conversation askGoalAmount(RespectingChestGui jobActionsGUI, Player viewer, Job job) 
	{
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new JobPartialCompletionAmountPrompt(this.messageService, this.jobService, job))
				.addConversationAbandonedListener(abandonedEvent -> 
				{
					if(!abandonedEvent.gracefulExit())
						return;

					int amountToUse = (int) abandonedEvent.getContext().getSessionData("Amount To Use");
					JobCompletionContext completionContext = (amountToUse == job.getGoal().getAmount()) ? JobCompletionContext.normal(job) : JobCompletionContext.partial(this.jobService.getPartialCompletionInfo(viewer, job, amountToUse));
					
					completeJob(viewer, job, completionContext);
				})
				.buildConversation(viewer);
	}

	private void completeJob(Player viewer, Job job, JobCompletionContext context) 
	{
		if(!checkJobAvailability(viewer, job))
			return;
		
		this.globalBoard.completeJob(job, viewer, context);

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
	private boolean checkJobAvailability(Player viewer, Job job) 
	{
		if(this.globalBoard.containsJob(job)) 
			return true;

		viewer.closeInventory();
		this.messageService.loadMessage(JOB_NOT_AVAILABLE_ANYMORE).sendTo(viewer);
		return false;
	}
}
