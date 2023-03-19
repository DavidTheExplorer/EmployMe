package dte.employme.conversations;

import static dte.employme.messages.MessageKey.CONVERSATION_ESCAPE_WORD;

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
				.withEscapeSequence(messageService.loadMessage(CONVERSATION_ESCAPE_WORD).first());
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
			messageService.loadMessage(messageToSend).sendTo(player);
		};
	}
}