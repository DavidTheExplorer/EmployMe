package dte.employme.guis.creation;

import static dte.employme.conversations.Conversations.createRewardRefundListener;
import static dte.employme.messages.MessageKey.GOAL_QUESTION;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.java.Predicates.negate;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import dte.employme.configs.GuiConfig;
import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.guis.creation.GoalCustomizationGUIFactory.GoalCustomizationGUI;
import dte.employme.rewards.Reward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.forwarding.ForwardingChestGui;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteBuilder;
import dte.employme.utils.inventoryframework.itempalette.ItemPaletteGUI;
import dte.employme.utils.java.MapBuilder;

public class GoalSelectionGUIFactory
{
	private final GuiConfig config;
	private final JobService jobService;
	private final MessageService messageService;

	public GoalSelectionGUIFactory(GuiConfig config, JobService jobService, MessageService messageService)
	{
		this.config = config;
		this.jobService = jobService;
		this.messageService = messageService;
	}

	public ChestGui create(Player viewer, GoalCustomizationGUI goalCustomizationGUI, Reward reward) 
	{
		ItemPaletteGUI palette = ItemPaletteBuilder.withAllItems()
				.named(this.config.getTitle())
				.filter(negate(material -> this.jobService.isBlacklistedAt(viewer.getWorld(), material)))
				.map(material -> toSelectableItem(viewer, material, goalCustomizationGUI))
				.withControlButtons(parseBackItem(), parseNextItem())
				.withSearchFeature(parseSearchItem(), createEnglishTypeConversationFactory(viewer, reward, goalCustomizationGUI))
				.build();
		
		//Due to the need for an Item Palette but with more methods(showsGoalCustomizationGUIOnClose()), a delegate is used and returned.
		GoalSelectionGUI gui = new GoalSelectionGUI(palette);
		
		gui.setOnClose(event -> 
		{
			if(!gui.showsGoalCustomizationGUIOnClose())
				return;
			
			goalCustomizationGUI.refundRewardOnClose(true);
			goalCustomizationGUI.show(viewer);
		});
		
		return palette;
	}

	private ItemStack parseSearchItem() 
	{
		return this.config.parseGuiItem("search").build().getItem();
	}

	private ItemStack parseBackItem() 
	{
		return this.config.parseGuiItem("back").build().getItem();
	}

	private ItemStack parseNextItem() 
	{
		return this.config.parseGuiItem("next").build().getItem();
	}

	private static GuiItem toSelectableItem(Player viewer, Material material, GoalCustomizationGUI goalCustomizationGUI)
	{
		return new GuiItemBuilder()
				.forItem(new ItemStack(material))
				.whenClicked(event -> 
				{
					goalCustomizationGUI.setType(material);
					goalCustomizationGUI.show(viewer);
				})
				.build();
	}

	private ConversationFactory createEnglishTypeConversationFactory(Player viewer, Reward reward, GoalCustomizationGUI goalCustomizationGUI) 
	{
		return Conversations.createFactory(this.messageService)
				.withFirstPrompt(new JobGoalPrompt(this.jobService, this.messageService, this.messageService.loadMessage(GOAL_QUESTION).first()))
				.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
				.addConversationAbandonedListener(createRewardRefundListener(this.messageService, JOB_SUCCESSFULLY_CANCELLED))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					//re-open the goal customization gui
					Material material = (Material) event.getContext().getSessionData("material");

					goalCustomizationGUI.refundRewardOnClose(true);
					goalCustomizationGUI.setType(material);
					goalCustomizationGUI.show(viewer);
				});
	}
	
	
	
	public class GoalSelectionGUI extends ForwardingChestGui
	{
		private boolean showGoalCustomizationGUIOnClose = true;
		
		public GoalSelectionGUI(ItemPaletteGUI goalSelectionGui)
		{
			super(goalSelectionGui);
		}
		
		public boolean showsGoalCustomizationGUIOnClose() 
		{
			return this.showGoalCustomizationGUIOnClose;
		}
	}
}