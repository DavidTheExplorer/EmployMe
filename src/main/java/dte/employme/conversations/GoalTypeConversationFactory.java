package dte.employme.conversations;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import dte.employme.inventories.GoalCustomizationGUI;
import dte.employme.job.prompts.JobGoalPrompt;
import dte.employme.messages.service.MessageService;

public class GoalTypeConversationFactory 
{
	private static final ConversationAbandonedListener REOPEN_CUSTOMIZATION_GUI = event -> 
	{
		if(!event.gracefulExit())
			return;

		Player player = (Player) event.getContext().getForWhom();
		Material material = (Material) event.getContext().getSessionData("material");
		GoalCustomizationGUI goalCustomizationGUI = (GoalCustomizationGUI) event.getContext().getSessionData("goal inventory");

		goalCustomizationGUI.setRefundRewardOnClose(true);
		goalCustomizationGUI.setType(material);
		goalCustomizationGUI.show(player);
	};

	public static ConversationFactory create(MessageService messageService) 
	{
		return Conversations.createFactory()
				.withLocalEcho(false)
				.withFirstPrompt(new JobGoalPrompt(messageService))
				.addConversationAbandonedListener(REOPEN_CUSTOMIZATION_GUI);
	}
}
