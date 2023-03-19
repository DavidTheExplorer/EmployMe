package dte.employme.guis.jobs.creation;

import static dte.employme.conversations.Conversations.refundReward;
import static dte.employme.messages.MessageKey.GUI_ITEM_PALETTE_TITLE;
import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_CANCELLED;
import static dte.employme.utils.java.Predicates.negate;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;

import dte.employme.conversations.Conversations;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.guis.ItemPaletteGUI;
import dte.employme.rewards.Reward;
import dte.employme.services.job.JobService;
import dte.employme.services.job.subscription.JobSubscriptionService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.employme.utils.java.MapBuilder;

public class ItemPaletteGoalGUI extends ItemPaletteGUI
{
	private boolean showGoalCustomizationGUIOnClose = true;

	public ItemPaletteGoalGUI(World world, JobService jobService, MessageService messageService, JobSubscriptionService jobSubscriptionService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
	{
		super(messageService.loadMessage(GUI_ITEM_PALETTE_TITLE).first(), 
				messageService, 
				getChooseableItem(goalCustomizationGUI), 
				negate(material -> jobService.isBlacklistedAt(world, material)),
				createEnglishTypeConversationFactory(messageService, jobService, reward, goalCustomizationGUI));

		setEnglishItemClickHandler(event -> this.showGoalCustomizationGUIOnClose = false);

		setOnClose(event -> 
		{
			if(!this.showGoalCustomizationGUIOnClose)
				return;

			goalCustomizationGUI.setRefundRewardOnClose(true);
			goalCustomizationGUI.show(event.getPlayer());
		});
	}

	private static Function<Material, GuiItem> getChooseableItem(GoalCustomizationGUI goalCustomizationGUI)
	{
		return material -> new GuiItemBuilder()
				.forItem(new ItemStack(material))
				.whenClicked(event -> 
				{
					goalCustomizationGUI.setType(material);
					event.getWhoClicked().closeInventory();
				})
				.build();
	}

	private static ConversationFactory createEnglishTypeConversationFactory(MessageService messageService, JobService jobService, Reward reward, GoalCustomizationGUI goalCustomizationGUI) 
	{
		return Conversations.createFactory(messageService)
				.withFirstPrompt(new JobGoalPrompt(jobService, messageService, messageService.loadMessage(ITEM_GOAL_FORMAT_QUESTION).first()))
				.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
				.addConversationAbandonedListener(refundReward(messageService, JOB_SUCCESSFULLY_CANCELLED))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					//re-open the goal customization gui
					Player player = (Player) event.getContext().getForWhom();
					Material material = (Material) event.getContext().getSessionData("material");

					goalCustomizationGUI.setRefundRewardOnClose(true);
					goalCustomizationGUI.setType(material);
					goalCustomizationGUI.show(player);
				});
	}
}
