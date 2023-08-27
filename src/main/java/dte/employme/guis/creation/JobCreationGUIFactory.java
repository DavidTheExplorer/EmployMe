package dte.employme.guis.creation;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobPaymentPrompt;
import dte.employme.job.creation.JobCreationContext;
import dte.employme.rewards.MoneyReward;
import dte.employme.services.message.MessageService;
import net.milkbowl.vault.economy.Economy;

public class JobCreationGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;
	private final Economy economy;
	private final ItemsRewardOfferGUIFactory itemsRewardOfferGUIFactory;
	private final GoalCustomizationGUIFactory goalCustomizationGUIFactory;

	public JobCreationGUIFactory(GuiConfig config, MessageService messageService, Economy economy, ItemsRewardOfferGUIFactory itemsRewardOfferGUIFactory, GoalCustomizationGUIFactory goalCustomizationGUIFactory)
	{
		this.config = config;
		this.messageService = messageService;
		this.economy = economy;
		this.goalCustomizationGUIFactory = goalCustomizationGUIFactory;
		this.itemsRewardOfferGUIFactory = itemsRewardOfferGUIFactory;
	}

	public ChestGui create(JobCreationContext creationContext)
	{
		ChestGui gui = new ChestGui(3, this.config.getTitle());

		//add panes
		gui.addPane(parseBackground());
		gui.addPane(parseJobTypesPane(creationContext));

		//register listeners
		gui.setOnTopClick(event -> event.setCancelled(true));

		return gui;
	}

	private Pane parseBackground() 
	{
		return createRectangle(Priority.LOWEST, 0, 0, 9, 3, this.config.parseGuiItem("background").build());
	}

	private OutlinePane parseJobTypesPane(JobCreationContext creationContext) 
	{
		OutlinePane pane = new OutlinePane(2, 1, 6, 1, Priority.LOW);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(3);

		//add the different job types icons
		pane.addItem(parseMoneyJobIcon(creationContext));
		pane.addItem(parseItemsJobIcon(creationContext));

		return pane;
	}

	private GuiItem parseMoneyJobIcon(JobCreationContext creationContext) 
	{
		Player viewer = creationContext.getEmployer();
		
		return this.config.parseGuiItem("money-job")
				.whenClicked(event -> 
				{
					viewer.closeInventory();
					createMoneyJobConversation(creationContext).begin();
				})
				.build();
	}

	private GuiItem parseItemsJobIcon(JobCreationContext creationContext) 
	{
		return this.config.parseGuiItem("items-job")
				.whenClicked(event -> this.itemsRewardOfferGUIFactory.create(creationContext).show(creationContext.getEmployer()))
				.build();
	}

	private Conversation createMoneyJobConversation(JobCreationContext creationContext) 
	{
		Player viewer = creationContext.getEmployer();
		
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new JobPaymentPrompt(this.economy, this.messageService))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					//set the money reward and continue to the Goal Customization GUI
					creationContext.setReward((MoneyReward) event.getContext().getSessionData("reward"));

					this.goalCustomizationGUIFactory.create(viewer, creationContext).show(viewer);
				})
				.buildConversation(viewer);
	}
}
