package dte.employme.conversations;

import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import dte.employme.EmployMe;
import dte.employme.job.rewards.Reward;

public class Conversations
{
	//Container of static methods
	private Conversations(){}
	
	public static final ConversationAbandonedListener RETURN_REWARD_TO_PLAYER = event -> 
	{
		if(event.gracefulExit())
			return;
		
		Reward reward = (Reward) event.getContext().getSessionData("reward");
		
		if(reward == null)
			return;
		
		Player player = (Player) event.getContext().getForWhom();
		reward.giveTo(player);
	};

	public static ConversationFactory createFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("cancel")
				.withPrefix(context -> EmployMe.CHAT_PREFIX + " ");
	}
}