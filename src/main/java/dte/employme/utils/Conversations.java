package dte.employme.utils;

import static dte.employme.messages.MessageKey.PREFIX;

import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;

import dte.employme.EmployMe;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;

public class Conversations
{
	//Container of static methods
	private Conversations(){}
	
	public static final ConversationAbandonedListener REFUND_REWARD_IF_ABANDONED = event -> 
	{
		if(event.gracefulExit())
			return;
		
		Reward reward = (Reward) event.getContext().getSessionData("Reward");
		
		reward.giveTo((OfflinePlayer) event.getContext().getForWhom());
	};

	public static ConversationFactory createFactory(MessageService messageService)
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> messageService.getMessage(PREFIX).first());
	}
}