package dte.employme.guis.jobs.creation;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_MONEY_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_MONEY_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.GUI_JOB_CREATION_TITLE;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

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
import dte.employme.rewards.MoneyReward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.items.ItemBuilder;
import net.milkbowl.vault.economy.Economy;

public class JobCreationGUI extends ChestGui
{
	private final MessageService messageService;
	private final ConversationFactory moneyJobConversationFactory;
	
	public JobCreationGUI(JobBoard jobBoard, MessageService messageService, Economy economy, JobService jobService)
	{
		super(3, messageService.loadMessage(GUI_JOB_CREATION_TITLE).first());
		
		this.messageService = messageService;
		
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
					
					new GoalCustomizationGUI(messageService, jobService, jobBoard, moneyReward).show(player);
				});
		
		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createOptionsPane());
	}
	
	private Pane createOptionsPane() 
	{
		OutlinePane pane = new OutlinePane(4, 1, 6, 1, Priority.LOW);
		pane.setOrientation(HORIZONTAL);
		
		//add the money job icon
		pane.addItem(new GuiItemBuilder()
				.forItem(new ItemBuilder(Material.GOLD_INGOT)
						.named(this.messageService.loadMessage(GUI_JOB_CREATION_MONEY_JOB_ICON_NAME).first())
						.withLore(this.messageService.loadMessage(GUI_JOB_CREATION_MONEY_JOB_ICON_LORE).toArray())
						.createCopy())
				.whenClicked(event -> 
				{
					Player player = (Player) event.getWhoClicked();

					player.closeInventory();
					this.moneyJobConversationFactory.buildConversation(player).begin();
				})
				.build());

		return pane;
	}
}