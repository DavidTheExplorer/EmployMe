package dte.employme.conversations;

import static dte.employme.messages.MessageKey.PREFIX;

import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;

import dte.employme.EmployMe;
import dte.employme.job.rewards.Reward;
import dte.employme.services.job.reward.JobRewardService;
import dte.employme.services.message.MessageService;

public class Conversations
{
	public static ConversationFactory createFactory(MessageService messageService)
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> messageService.getMessage(PREFIX).first());
	}

	public static ConversationAbandonedListener refundRewardIfAbandoned(JobRewardService jobRewardService) 
	{
		return event ->
		{
			if(event.gracefulExit())
				return;

			Reward reward = (Reward) event.getContext().getSessionData("Reward");

			if(reward == null)
				return;

			jobRewardService.refund((OfflinePlayer) event.getContext().getForWhom(), reward);
		};
	}
}