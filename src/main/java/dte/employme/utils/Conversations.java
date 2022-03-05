package dte.employme.utils;

import static dte.employme.messages.MessageKey.PREFIX;

import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;

import dte.employme.EmployMe;
import dte.employme.job.rewards.Reward;
import dte.employme.job.rewards.service.RewardService;
import dte.employme.messages.service.MessageService;

public class Conversations
{
	public static ConversationAbandonedListener refundRewardIfAbandoned(RewardService rewardService) 
	{
		return event ->
		{
			if(event.gracefulExit())
				return;
			
			Reward reward = (Reward) event.getContext().getSessionData("Reward");
			
			rewardService.refund((OfflinePlayer) event.getContext().getForWhom(), reward);
		};
	}

	public static ConversationFactory createFactory(MessageService messageService)
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> messageService.getMessage(PREFIX).first());
	}
}