package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_TITLE;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.job.prompts.JobPaymentPrompt;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.rewards.service.RewardService;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.Conversations;
import dte.employme.utils.items.ItemBuilder;
import net.milkbowl.vault.economy.Economy;

public class JobCreationGUI extends ChestGui
{
	private final JobBoard jobBoard;
	private final MessageService messageService;
	private final PlayerContainerService playerContainerService;
	private final ConversationFactory moneyJobConversationFactory;
	private final RewardService rewardService;
	
	public JobCreationGUI(JobBoard jobBoard, MessageService messageService, Economy economy, PlayerContainerService playerContainerService, RewardService rewardService)
	{
		super(3, messageService.getMessage(INVENTORY_JOB_CREATION_TITLE).first());
		
		this.jobBoard = jobBoard;
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
		this.rewardService = rewardService;
		
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
					
					new GoalCustomizationGUI(messageService, rewardService, jobBoard, moneyReward).show(player);
				});
		
		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createOptionsPane());
		update();
	}
	
	private Pane createOptionsPane() 
	{
		OutlinePane pane = new OutlinePane(2, 1, 6, 1, Priority.LOW);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(3);
		
		//add the money job icon
		pane.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT)
				.named(this.messageService.getMessage(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME).first())
				.withLore(this.messageService.getMessage(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE).toArray())
				.createCopy(), 
				event -> 
		{
			Player player = (Player) event.getWhoClicked();
			
			player.closeInventory();
			this.moneyJobConversationFactory.buildConversation(player).begin();
		}));
		
		//add the items job icon
		pane.addItem(new GuiItem(new ItemBuilder(Material.CHEST)
				.named(this.messageService.getMessage(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME).first())
				.withLore(this.messageService.getMessage(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE).toArray())
				.createCopy(),
				event -> new ItemsRewardOfferGUI(this.jobBoard, this.messageService, this.playerContainerService, this.rewardService).show(event.getWhoClicked())));
		
		return pane;
	}
}