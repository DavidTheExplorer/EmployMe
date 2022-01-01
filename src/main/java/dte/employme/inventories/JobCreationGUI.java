package dte.employme.inventories;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.utils.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.InventoryUtils.createWall;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.job.prompts.JobPaymentPrompt;
import dte.employme.job.rewards.MoneyReward;
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
	
	public JobCreationGUI(JobBoard jobBoard, MessageService messageService, Economy economy, PlayerContainerService playerContainerService)
	{
		super(3, "Create a new Job");
		
		this.jobBoard = jobBoard;
		this.messageService = messageService;
		this.playerContainerService = playerContainerService;
		
		//init the goal's type conversation factory
		this.moneyJobConversationFactory = Conversations.createFactory()
				.withFirstPrompt(new JobPaymentPrompt(economy, messageService))
				.addConversationAbandonedListener(event -> 
				{
					//if the player disconnected(etc) then the goal customization gui can't be open
					if(!event.gracefulExit())
						return;
					
					Player player = (Player) event.getContext().getForWhom();
					MoneyReward moneyReward = (MoneyReward) event.getContext().getSessionData("reward");
					
					new GoalCustomizationGUI(messageService, jobBoard, moneyReward).show(player);
				});
		
		setOnTopClick(event -> event.setCancelled(true));
		addPane(createRectangle(Priority.LOWEST, 0, 0, 9, 3, new GuiItem(createWall(Material.BLACK_STAINED_GLASS_PANE))));
		addPane(createOptionsPane(Priority.LOW));
		update();
	}
	
	private OutlinePane createOptionsPane(Priority priority) 
	{
		OutlinePane pane = new OutlinePane(2, 1, 6, 1, priority);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(3);
		
		//add the money job icon
		pane.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT)
				.named(GOLD + "Money Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay a certain amount of money.")
				.createCopy(), 
				event -> 
		{
			Player player = (Player) event.getWhoClicked();
			
			player.closeInventory();
			this.moneyJobConversationFactory.buildConversation(player).begin();
		}));
		
		//add the items job icon
		pane.addItem(new GuiItem(new ItemBuilder(Material.CHEST)
				.named(AQUA + "Items Job")
				.withLore(WHITE + "Click to offer a Job for which", WHITE + "You will pay with resources.")
				.createCopy(),
				event -> new ItemsRewardOfferGUI(this.jobBoard, this.messageService, this.playerContainerService).show(event.getWhoClicked())));
		
		return pane;
	}
}