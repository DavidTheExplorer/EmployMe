package dte.employme.conversations;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.inventories.GoalCustomizationGUI;
import dte.employme.job.prompts.JobGoalPrompt;
import dte.employme.job.prompts.JobPaymentPrompt;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;
import net.milkbowl.vault.economy.Economy;

public class Conversations
{
	private final ConversationFactory moneyJobConversationFactory;
	
	private static final ConversationAbandonedListener RETURN_REWARD_TO_PLAYER = event -> 
	{
		Player player = (Player) event.getContext().getForWhom();
		Reward reward = (Reward) event.getContext().getSessionData("reward");

		if(reward == null || event.gracefulExit())
			return;
		
		reward.giveTo(player);
	};

	public Conversations(JobBoard globalJobBoard, PlayerContainerService playerContainerService, MessageService messageService, Economy economy)
	{
		this.moneyJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobPaymentPrompt(economy, messageService))
				.addConversationAbandonedListener(RETURN_REWARD_TO_PLAYER)
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;
					
					Player player = (Player) event.getContext().getForWhom();
					MoneyReward moneyReward = (MoneyReward) event.getContext().getSessionData("reward");
					
					new GoalCustomizationGUI(createTypeConversationFactory(messageService), messageService, globalJobBoard, moneyReward).show(player);
				});
	}

	public Conversation ofMoneyJobCreation(Player employer)
	{
		return this.moneyJobConversationFactory.buildConversation(employer);
	}
	
	public ConversationFactory createTypeConversationFactory(MessageService messageService) 
	{
		return createConversationFactory()
				.withLocalEcho(false)
				.withFirstPrompt(new JobGoalPrompt(messageService))
				.addConversationAbandonedListener(event -> 
				{
					if(!event.gracefulExit())
						return;

					Player player = (Player) event.getContext().getForWhom();
					Material material = (Material) event.getContext().getSessionData("material");
					GoalCustomizationGUI goalCustomizationGUI = (GoalCustomizationGUI) event.getContext().getSessionData("goal inventory");
					
					goalCustomizationGUI.setRefundRewardOnClose(true);
					goalCustomizationGUI.setType(material);
					goalCustomizationGUI.show(player);
				});
	}

	public static ConversationFactory createConversationFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> EmployMe.CHAT_PREFIX + " ");
	}
}
