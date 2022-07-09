package dte.employme.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;

import dte.employme.conversations.Conversations;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.java.MapBuilder;

public class TypeItemPaletteGUI extends ItemPaletteGUI
{
	private boolean showGoalCustomizationGUIOnClose = true;

	public TypeItemPaletteGUI(MessageService messageService, JobRewardService jobRewardService, GoalCustomizationGUI goalCustomizationGUI, Reward reward)
	{
		super(messageService,
				
				item -> new GuiItem(item, event -> 
				{
					goalCustomizationGUI.setType(item.getType());
					event.getWhoClicked().closeInventory();
				}),

				typeConversationFactory -> 
				typeConversationFactory
				.withInitialSessionData(new MapBuilder<Object, Object>().put("Reward", reward).build())
				.addConversationAbandonedListener(Conversations.refundRewardIfAbandoned(jobRewardService))
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
				}));

		setEnglishItemClickHandler(event -> this.showGoalCustomizationGUIOnClose = false);

		setOnClose(event -> 
		{
			if(!this.showGoalCustomizationGUIOnClose)
				return;

			goalCustomizationGUI.setRefundRewardOnClose(true);
			goalCustomizationGUI.show(event.getPlayer());
		});
	}
}