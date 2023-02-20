package dte.employme.conversations;

import static dte.employme.messages.MessageKey.PREFIX;

import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.messages.MessageKey;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;

public class Conversations
{
	public static ConversationFactory createFactory(MessageService messageService)
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence(messageService.getMessage(MessageKey.CONVERSATION_ESCAPE_WORD).first())
				.withPrefix(context -> messageService.getMessage(PREFIX).first());
	}

	public static ConversationAbandonedListener refundReward(MessageService messageService, MessageKey messageToSend) 
	{
		return event ->
		{
			if(event.gracefulExit())
				return;

			Reward reward = (Reward) event.getContext().getSessionData("Reward");
			Player player = (Player) event.getContext().getForWhom();

			if(reward == null)
				return;
			
			reward.giveTo(player);
			messageService.getMessage(messageToSend).sendTo(player);
		};
	}
}