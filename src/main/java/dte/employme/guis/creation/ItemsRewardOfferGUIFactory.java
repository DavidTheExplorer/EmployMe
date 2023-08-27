package dte.employme.guis.creation;

import static dte.employme.messages.MessageKey.PREFIX;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;

import dte.employme.configs.GuiConfig;
import dte.employme.job.creation.JobCreationContext;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.InventoryUtils;

public class ItemsRewardOfferGUIFactory
{
	private final GuiConfig config;
	private final MessageService messageService;
	private final GoalCustomizationGUIFactory goalCustomizationGUIFactory;

	public ItemsRewardOfferGUIFactory(GuiConfig config, MessageService messageService, GoalCustomizationGUIFactory goalCustomizationGUIFactory) 
	{
		this.config = config;
		this.messageService = messageService;
		this.goalCustomizationGUIFactory = goalCustomizationGUIFactory;
	}

	public ItemsRewardOfferGUI create(JobCreationContext creationContext) 
	{
		ItemsRewardOfferGUI gui = new ItemsRewardOfferGUI(6, this.config.getTitle());
		
		//the confirmation button depends on the gui - so init() must be called
		GuiItem confirmationButton = parseConfirmationButton(gui, creationContext);
		gui.init(confirmationButton.getItem());

		//add panes
		gui.addPane(createConfirmationButtonPane(confirmationButton));

		//register listeners
		gui.setOnClose(createReturnItemsListener(creationContext.getEmployer(), gui));
		
		return gui;
	}

	private StaticPane createConfirmationButtonPane(GuiItem confirmationButton) 
	{
		StaticPane pane = new StaticPane(0, 5, 1, 1, Priority.LOW);
		pane.addItem(confirmationButton, Slot.fromXY(0, 0));

		return pane;
	}

	private GuiItem parseConfirmationButton(ItemsRewardOfferGUI gui, JobCreationContext creationContext) 
	{
		return this.config.parseGuiItem("confirm-button")
				.whenClicked(event -> 
				{
					Player viewer = creationContext.getEmployer();
					List<ItemStack> offeredItems = gui.getOfferedItems();

					//block confirming if the player didn't offer anything
					if(offeredItems.isEmpty())
						return;

					//disable the items return listener
					gui.setOnClose(closeEvent -> {});
					
					//set the items reward and continue to the Goal Customization GUI
					creationContext.setReward(new ItemsReward(offeredItems));

					this.goalCustomizationGUIFactory.create(viewer, creationContext).show(viewer);
				})
				.build();
	}
	
	private Consumer<InventoryCloseEvent> createReturnItemsListener(Player viewer, ItemsRewardOfferGUI gui)
	{
		return event -> 
		{
			ItemStack[] offeredItems = gui.getOfferedItems().toArray(new ItemStack[0]);
			
			//alert the employer if no items were offered
			if(offeredItems.length == 0) 
			{
				this.config.getText("no-items-offered-warning")
				.inject("prefix", this.messageService.loadMessage(PREFIX).first())
				.sendTo(viewer);
				
				return;
			}

			viewer.closeInventory();
			viewer.getInventory().addItem(offeredItems);
		};
	}



	public class ItemsRewardOfferGUI extends ChestGui
	{
		private ItemStack confirmationButton;

		public ItemsRewardOfferGUI(int rows, String title)
		{
			super(rows, title);
		}

		void init(ItemStack confirmationButton) 
		{
			this.confirmationButton = confirmationButton;
		}

		public List<ItemStack> getOfferedItems() 
		{
			return InventoryUtils.itemsStream(getInventory(), true)
					.filter(item -> !item.equals(this.confirmationButton))
					.collect(toList());
		}
	}
}
