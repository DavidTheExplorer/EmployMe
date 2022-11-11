package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_ITEMS_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_ITEMS_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_MONEY_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_MONEY_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createItemPane;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobPaymentPrompt;
import dte.employme.job.JobType;
import dte.employme.rewards.MoneyReward;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.services.playercontainer.PlayerContainerService;
import dte.employme.utils.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import net.milkbowl.vault.economy.Economy;

public class JobCreationGUI extends ChestGui
{
	private final JobBoard jobBoard;
	private final MessageService messageService;
	private final PlayerContainerService playerContainerService;
	private final JobSubscriptionService jobSubscriptionService;
	private final ConversationFactory moneyJobConversationFactory;

	public JobCreationGUI(JobBoard jobBoard, MessageService messageService, JobSubscriptionService jobSubscriptionService, Economy economy, List<JobType> jobTypes, PlayerContainerService playerContainerService)
	{
		super(3, messageService.getMessage(GUI_JOB_CREATION_TITLE).first());

		this.jobBoard = jobBoard;
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
		this.jobSubscriptionService = jobSubscriptionService;

		//init the goal's type conversation factory
		this.moneyJobConversationFactory = Conversations.createFactory(messageService)
				.withFirstPrompt(new JobPaymentPrompt(economy, messageService))
				.addConversationAbandonedListener(event -> 
				{
					//if the player disconnected(etc) then the goal customization gui can't be open
					if(!event.gracefulExit())
						return;

					Player player = (Player) event.getContext().getForWhom();
					MoneyReward moneyReward = (MoneyReward) event.getContext().getSessionData("reward");

					new GoalCustomizationGUI(messageService, jobSubscriptionService, jobBoard, moneyReward).show(player);
				});

		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createOptionsPane(jobTypes));
		update();
	}

	private Pane createOptionsPane(List<JobType> jobTypes) 
	{
		//if there's just 1 job type, put it in the middle of the screen
		if(jobTypes.size() == 1) 
			return createItemPane(4, 1, createIcon(jobTypes.get(0)));

		OutlinePane pane = new OutlinePane(2, 1, 6, 1, Priority.LOW);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(3);
		
		jobTypes.stream()
		.map(this::createIcon)
		.forEach(pane::addItem);

		return pane;
	}
	
	private GuiItem createIcon(JobType jobType) 
	{
		switch(jobType) 
		{
		case MONEY:
			return createMoneyJobIcon();
		case ITEMS:
			return createItemsJobIcon();
		default:
			throw new IllegalArgumentException("The provided job type doesn't have an icon!");
		}
	}

	private GuiItem createMoneyJobIcon() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.GOLD_INGOT)
						.named(this.messageService.getMessage(GUI_JOB_CREATION_MONEY_JOB_ICON_NAME).first())
						.withLore(this.messageService.getMessage(GUI_JOB_CREATION_MONEY_JOB_ICON_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();

					player.closeInventory();
					this.moneyJobConversationFactory.buildConversation(player).begin();
				})
				.build();
	}

	private GuiItem createItemsJobIcon() 
	{
		return new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.CHEST)
						.named(this.messageService.getMessage(GUI_JOB_CREATION_ITEMS_JOB_ICON_NAME).first())
						.withLore(this.messageService.getMessage(GUI_JOB_CREATION_ITEMS_JOB_ICON_LORE).toArray())
						.createCopy())
				.whenClicked(event -> new ItemsRewardOfferGUI(this.jobBoard, this.messageService, this.playerContainerService, this.jobSubscriptionService).show(event.getWhoClicked()))
				.build();
	}
}