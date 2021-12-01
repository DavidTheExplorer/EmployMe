package dte.employme.conversations;

import java.util.Collection;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.job.prompts.JobGoalPrompt;
import dte.employme.job.prompts.JobPaymentPrompt;
import dte.employme.job.prompts.JobPostedMessagePrompt;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;
import net.milkbowl.vault.economy.Economy;

public class Conversations
{
	private final PlayerContainerService playerContainerService;
	private final ConversationFactory moneyJobConversationFactory, itemsJobConversationFactory;
	
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
		this.playerContainerService = playerContainerService;

		this.moneyJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPaymentPrompt(globalJobBoard, economy, messageService), messageService))
				.addConversationAbandonedListener(RETURN_REWARD_TO_PLAYER);

		this.itemsJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPostedMessagePrompt(globalJobBoard, economy), messageService))
				.addConversationAbandonedListener(RETURN_REWARD_TO_PLAYER);
	}

	public Conversation ofMoneyJobCreation(Player employer)
	{
		return this.moneyJobConversationFactory.buildConversation(employer);
	}

	public Conversation ofItemsJobCreation(Player employer, Collection<ItemStack> offeredItems)
	{
		Conversation conversation = this.itemsJobConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("reward", new ItemsReward(offeredItems, this.playerContainerService));

		return conversation;
	}

	private static ConversationFactory createConversationFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> EmployMe.CHAT_PREFIX + " ");
	}
}
